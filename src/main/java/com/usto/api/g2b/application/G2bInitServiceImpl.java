package com.usto.api.g2b.application;

import com.usto.api.g2b.domain.model.G2bSync;
import com.usto.api.g2b.domain.model.G2bSyncMapper;
import com.usto.api.g2b.domain.model.ShoppingMallOpenApiClient;
import com.usto.api.g2b.domain.service.G2bItemCategoryService;
import com.usto.api.g2b.domain.service.G2bItemService;
import com.usto.api.g2b.domain.service.G2bSyncService;
import com.usto.api.g2b.presentation.dto.response.ShoppingMallEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class G2bInitServiceImpl {

    private final G2bSyncService g2bStgService;
    private final G2bItemService g2bItemService;
    private final G2bItemCategoryService g2bItemCategoryService;
    private final ShoppingMallOpenApiClient client; //API연동

    @Lazy
    @Autowired
    private G2bInitServiceImpl self;

    private static final String INQRY_DIV = "1";     // 등록일자 기준
    private static final int NUM_OF_ROWS = 50;     // 페이지 사이즈
    private static final long PAGE_SLEEP_MS = 200;

    public int syncInit1y(LocalDate seedEndDate) {
        if (seedEndDate == null) {
            throw new IllegalArgumentException("seedEndDate is required");
        }

        LocalDate end = seedEndDate;
        LocalDate begin = end.minusYears(1).plusDays(1);

        int totalAffected = 0;

        for (DateRange r : splitByDay(begin, end)) {
            totalAffected += self.syncOneRange(r);
        }
        return totalAffected;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int syncOneRange(DateRange r) {
        g2bStgService.truncate();

        List<ShoppingMallEnvelope.Item> items = fetchAllByRange(r);
        List<G2bSync> rows = G2bSyncMapper.toG2bSync(items);

        if (rows.isEmpty()) {
            return 0;
        }

        g2bStgService.bulkInsert(rows);

        int detail = g2bItemService.updateDetail();
        int master = g2bItemCategoryService.updateMaster();
        return detail + master;
    }

    private List<ShoppingMallEnvelope.Item> fetchAllByRange(DateRange r) {
        var first = client.fetch("1", String.valueOf(NUM_OF_ROWS), INQRY_DIV, r.beginYmd(), r.endYmd());
        var items = new ArrayList<>(first.items());

        int total = first.totalCount();
        int pages = (int) Math.ceil(total / (double) NUM_OF_ROWS);

        for (int p = 2; p <= pages; p++) {
            sleepQuietly(PAGE_SLEEP_MS);
            var page = client.fetch(String.valueOf(p), String.valueOf(NUM_OF_ROWS), INQRY_DIV, r.beginYmd(), r.endYmd());
            items.addAll(page.items());
        }
        return items;
    }

    private void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private List<DateRange> splitByDay(LocalDate start, LocalDate endInclusive) {
        List<DateRange> out = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(endInclusive)) {
            out.add(DateRange.of(cursor, cursor));
            cursor = cursor.plusDays(1);
        }
        return out;
    }

    public record DateRange(LocalDate begin, LocalDate end, String beginYmd, String endYmd) {
        static DateRange of(LocalDate b, LocalDate e) {
            var f = java.time.format.DateTimeFormatter.BASIC_ISO_DATE;
            return new DateRange(b, e, b.format(f), e.format(f));
        }
    }
}
