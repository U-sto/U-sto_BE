package com.usto.api.g2b.application;

import com.usto.api.common.utils.ShoppingMallEnvelope;
import com.usto.api.common.utils.ShoppingMallOpenApiClient;
import com.usto.api.g2b.domain.model.SyncResult;
import com.usto.api.g2b.infrastructure.mapper.G2bStgMapper;
import com.usto.api.g2b.domain.model.G2bStg;
import com.usto.api.g2b.domain.repository.G2bItemCategoryRepository;
import com.usto.api.g2b.domain.repository.G2bItemRepository;
import com.usto.api.g2b.domain.repository.G2bStgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@Service
@RequiredArgsConstructor
public class G2bSyncApplication {

    private final G2bStgRepository g2bStgRepository;
    private final G2bItemRepository g2bItemRepository;
    private final G2bItemCategoryRepository g2bItemCategoryRepository;
    private final ShoppingMallOpenApiClient client; //API연동

    //이쪽 값을 정의해야할거같다.
    private static final String PAGE_NO = "1";        // 페이지 번호 (1페이지부터 조회해야 함)
    private static final String INQRY_DIV = "1";     // 등록일자 기준
    private static final int NUM_OF_ROWS = 10;     // 페이지 사이즈 (공공데이터포털 제한 최대가 10)
    private static final String ACTOR = "SYSTEM";

    @Transactional
    public SyncResult syncDaily() {
        LocalDate now = LocalDate.now();
        String begin = now.minusDays(2).format(DateTimeFormatter.BASIC_ISO_DATE); // 그저께 (시작일)
        String end = now.minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);   // 어제 (종료일) <- 이렇게 하는게 최선(오픈API 업데이트의 한계)
        return syncLatest(begin, end);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public SyncResult syncLatest(String begin , String end) {
        log.info("=== Sync Start: {} ~ {} ===", begin, end);

        // 외부 수집
        List<ShoppingMallEnvelope.Item> items = fetch(begin, end);
        int fetched = items.size();
        log.info("Fetched Total Items: : {}~{} , count :{}", begin, end, items.size());

        if (items.isEmpty()) {
            return SyncResult.empty(begin, end, 0);
        }

        // 도메인 변환 & 중복 제거 (핵심 로직)
        List<G2bStg> distinctDomainList = items.stream()
                .map(G2bStgMapper::toDomain) // 변환
                .filter(Objects::nonNull)    // Null 제거
                .collect(Collectors.toMap(
                        G2bStg::getG2bDCd,                  // Key: 물품식별코드 (PK)
                        stg -> stg,                         // Value: 객체
                        (existing, replacement) -> existing // 중복 시 기존 것 유지
                ))
                .values()
                .stream()
                .collect(Collectors.toList());

        log.info("Deduplicated Items to Insert: {} (Duplicates removed: {})",
                distinctDomainList.size(),
                items.size() - distinctDomainList.size());

        int deduped = distinctDomainList.size();
        int duplicated = fetched - deduped;

        // 스테이징테이블 비우기 (데이터 준비 후 삭제)
        g2bStgRepository.delete();

        if (!distinctDomainList.isEmpty()) {
            g2bStgRepository.bulkInsert(distinctDomainList);
        }

        // DB에 추가 사항 반영
        int countOfInsertedCategory = g2bItemCategoryRepository.insertCategory(ACTOR);
        int countOfInsertedItem = g2bItemRepository.insertItems(ACTOR);

        // DB에 수정 사항 반영
        int countOfUpdatedCategory = g2bItemCategoryRepository.updateCategory(ACTOR);
        int countOfUpdatedItem = g2bItemRepository.updateItems(ACTOR);

        long changed  = g2bStgRepository.countChanged();

        //스테이징 테이블 순번 초기화
        g2bStgRepository.resetId();

        return new SyncResult(
                begin,
                end,
                fetched,
                deduped,
                duplicated,
                countOfInsertedCategory,
                countOfInsertedItem,
                countOfUpdatedCategory,
                countOfUpdatedItem,
                changed
        );
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
            log.info("Fetching page {}/{}", p, pages);
            var page = client.fetch(String.valueOf(p), String.valueOf(NUM_OF_ROWS), INQRY_DIV, begin, end);
            if (page != null && page.items() != null) {
                items.addAll(page.items());
            }
        }
        return items;
    }
}
