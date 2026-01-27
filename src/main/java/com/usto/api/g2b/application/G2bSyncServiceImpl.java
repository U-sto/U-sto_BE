package com.usto.api.g2b.application;

import com.usto.api.common.utils.ShoppingMallEnvelope;
import com.usto.api.common.utils.ShoppingMallOpenApiClient;
import com.usto.api.g2b.infrastructure.entity.G2bStgMapper;
import com.usto.api.g2b.domain.model.G2bStg;
import com.usto.api.g2b.domain.service.G2bItemCategoryService;
import com.usto.api.g2b.domain.service.G2bItemService;
import com.usto.api.g2b.domain.service.G2bStgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@Service
@RequiredArgsConstructor
public class G2bSyncServiceImpl {

    private final G2bStgService g2bStgService;
    private final G2bItemService g2bItemService;
    private final G2bItemCategoryService g2bItemCategoryService;
    private final ShoppingMallOpenApiClient client; //API연동

    //이쪽 값을 정의해야할거같다.
    private static final String PAGE_NO = "1";        // 페이지 번호 (1페이지부터 조회해야 함)
    private static final String INQRY_DIV = "1";     // 등록일자 기준
    private static final int NUM_OF_ROWS = 10000;     // 페이지 사이즈 (테스트 성공 값인 10,000 권장)
    private static final String ACTOR = "SYSTEM";

    @Transactional
    public long syncDaily() {
        LocalDate now = LocalDate.now();
        String begin = now.minusDays(2).format(DateTimeFormatter.BASIC_ISO_DATE); // 그저께 (시작일)
        String end = now.minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);   // 어제 (종료일)
        return syncLatest(begin, end);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public long syncLatest(String begin , String end) {

        // 외부 수집
        List<ShoppingMallEnvelope.Item> items = fetch(begin, end);
        log.info("Period : {}~{} , count :{}", begin, end, items.size());
        
        if (items.isEmpty()) {
            return 0;
        }

        // 도메인 변환 (Stream 활용)
        List<G2bStg> domainList = items.stream()
                .map(G2bStgMapper::toDomain)
                .filter(Objects::nonNull)
                .toList();

        // 스테이징테이블 비우기 (데이터 준비 후 삭제)
        g2bStgService.truncate();

        // 분할 저장 (Batch Insert)-터지지 않게
        int batchSize =1000;
        for (int i = 0; i < domainList.size(); i+= batchSize) {
            g2bStgService.bulkInsert(domainList.subList(i, Math.min(i + batchSize, domainList.size())));
        }

        // DB에 추가 사항 반영
        g2bItemCategoryService.insertCategory(ACTOR);
         g2bItemService.insertItems(ACTOR);

        // DB에 수정 사항 반영
        g2bItemCategoryService.updateCategory(ACTOR);
        g2bItemService.updateItems(ACTOR);

        long count = g2bStgService.countChanged();


        
        return count;
    }

    private List<ShoppingMallEnvelope.Item> fetch(String begin, String end) {
        log.info("G2B API Request - Date: {} ~ {}, Page: {}, Rows: {}", begin, end, PAGE_NO, NUM_OF_ROWS);

        var first = client.fetch(PAGE_NO, String.valueOf(NUM_OF_ROWS), INQRY_DIV, begin, end);
        if (first == null || first.items() == null) {
            return new ArrayList<>();
        }
        var items = new ArrayList<>(first.items());

        int total = first.totalCount();
        log.info("G2B API Response - Total Count: {}", total);
        int pages = (int) Math.ceil(total / (double) NUM_OF_ROWS);

        for (int p = 2; p <= pages; p++) {
            var page = client.fetch(String.valueOf(p), String.valueOf(NUM_OF_ROWS), INQRY_DIV, begin, end);
            if (page != null && page.items() != null) {
                items.addAll(page.items());
            }
        }
        return items;
    }
}
