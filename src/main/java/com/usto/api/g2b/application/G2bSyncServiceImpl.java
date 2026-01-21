package com.usto.api.g2b.application;

import com.usto.api.g2b.domain.model.G2bSyncResult;
import com.usto.api.g2b.domain.model.StgPriceRowMapper;
import com.usto.api.g2b.domain.model.ShoppingMallOpenApiClient;
import com.usto.api.g2b.domain.model.StgPriceRow;
import com.usto.api.g2b.domain.service.G2bItemService;
import com.usto.api.g2b.domain.service.G2bStgService;
import com.usto.api.g2b.presentation.dto.response.G2bSyncResponseDto;
import com.usto.api.g2b.presentation.dto.response.ShoppingMallEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class G2bSyncServiceImpl {

    private final G2bStgService g2bStgService;
    private final G2bItemService g2bItemService;
    private final ShoppingMallOpenApiClient client; //API연동

    private static final String INQRY_DIV = "1";     // 등록일자 기준
    private static final int NUM_OF_ROWS = 1000;     // 페이지 사이즈

    @Transactional
    public G2bSyncResponseDto syncLatest() {
        List<ShoppingMallEnvelope.Item> items = fetchAll();
        List<StgPriceRow> rows = StgPriceRowMapper.toStgRows(items);

        g2bStgService.truncate();
        g2bStgService.bulkInsert(rows);

        var changed = g2bItemService.updateChangedPricesWithDiff();
        return G2bSyncResult.of(changed);    }

    private List<ShoppingMallEnvelope.Item> fetchAll() {
        var first = client.fetch(
                "1",
                String.valueOf(NUM_OF_ROWS),
                INQRY_DIV
        );

        var items = new ArrayList<>(first.items());

        int total = first.totalCount();
        int pages = (int) Math.ceil(total / (double) NUM_OF_ROWS);

        for (int p = 2; p <= pages; p++) {
            var page = client.fetch(
                    String.valueOf(p),
                    String.valueOf(NUM_OF_ROWS),
                    INQRY_DIV
            );
            items.addAll(page.items());
        }
        return items;
    }
}
