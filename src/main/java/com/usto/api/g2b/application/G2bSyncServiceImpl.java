package com.usto.api.g2b.application;

import com.usto.api.g2b.domain.model.StgPriceRowMapper;
import com.usto.api.g2b.domain.model.ShoppingMallOpenApiClient;
import com.usto.api.g2b.domain.model.StgPriceRow;
import com.usto.api.g2b.domain.service.G2bItemService;
import com.usto.api.g2b.domain.service.G2bStgService;
import com.usto.api.g2b.presentation.dto.response.ShoppingMallEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private static final int AUTO_SYNC_DAYS = 1;

    @Transactional
    public int syncLatest() {
        DateRange range = DateRange.recentDays(AUTO_SYNC_DAYS);
        List<ShoppingMallEnvelope.Item> items = fetchAll(range);
        List<StgPriceRow> rows = StgPriceRowMapper.toStgRows(items);

        g2bStgService.truncate();
        g2bStgService.bulkInsert(rows);

        return g2bItemService.updateChangedPrices();
    }

    private List<ShoppingMallEnvelope.Item> fetchAll(DateRange range) {
        var first = client.fetch(
                "1",
                String.valueOf(NUM_OF_ROWS),
                INQRY_DIV,
                range.begin(),
                range.end()
        );

        var items = new ArrayList<>(first.items());

        int total = first.totalCount();
        int pages = (int) Math.ceil(total / (double) NUM_OF_ROWS);

        for (int p = 2; p <= pages; p++) {
            var page = client.fetch(
                    String.valueOf(p),
                    String.valueOf(NUM_OF_ROWS),
                    INQRY_DIV,
                    range.begin(),
                    range.end()
            );
            items.addAll(page.items());
        }
        return items;
    }

    public record DateRange(String begin, String end) {

        public static DateRange recentDays(int days) {
            LocalDate end = LocalDate.now().minusDays(1);
            LocalDate begin = end.minusDays(days - 1);

            DateTimeFormatter f = DateTimeFormatter.BASIC_ISO_DATE;
            return new DateRange(begin.format(f), end.format(f));
        }
    }
}
