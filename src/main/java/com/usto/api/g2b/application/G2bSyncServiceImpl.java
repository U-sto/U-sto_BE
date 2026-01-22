package com.usto.api.g2b.application;

import com.usto.api.g2b.domain.model.G2bSyncMapper;
import com.usto.api.g2b.domain.model.ShoppingMallOpenApiClient;
import com.usto.api.g2b.domain.model.G2bSync;
import com.usto.api.g2b.domain.service.G2bItemCategoryService;
import com.usto.api.g2b.domain.service.G2bItemService;
import com.usto.api.g2b.domain.service.G2bSyncService;
import com.usto.api.g2b.presentation.dto.response.ShoppingMallEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class G2bSyncServiceImpl {

    private final G2bSyncService g2bStgService;
    private final G2bItemService g2bItemService;
    private final G2bItemCategoryService g2bItemCategoryService;
    private final ShoppingMallOpenApiClient client; //API연동

    private static final String INQRY_DIV = "1";     // 등록일자 기준
    private static final int NUM_OF_ROWS = 300;     // 페이지 사이즈

    @Transactional
    public int syncLatest() {

        g2bStgService.truncate();

        List<ShoppingMallEnvelope.Item> items = fetch();
        List<G2bSync> rows = G2bSyncMapper.toG2bSync(items);

        g2bStgService.bulkInsert(rows);

        int masterChanges = g2bItemCategoryService.updateMaster();
        int detailChanges = g2bItemService.updateDetail();

        return detailChanges+masterChanges;
    }

    private List<ShoppingMallEnvelope.Item> fetch() {
        var first = client.fetch("1", String.valueOf(NUM_OF_ROWS), INQRY_DIV);
        var items = new ArrayList<>(first.items());

        int total = first.totalCount();
        int pages = (int) Math.ceil(total / (double) NUM_OF_ROWS);

        for (int p = 2; p <= pages; p++) {
            var page = client.fetch(String.valueOf(p), String.valueOf(NUM_OF_ROWS), INQRY_DIV);
            items.addAll(page.items());
        }
        return items;
    }
}
