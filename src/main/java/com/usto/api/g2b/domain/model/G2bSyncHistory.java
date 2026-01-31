package com.usto.api.g2b.domain.model;

import java.time.LocalDateTime;

public record G2bSyncHistory(
        Long syncHisId,
        boolean stsYn,

        // SyncResult 순서 유지
        String begin,
        String end,
        int fetched,
        int deduped,
        int duplicated,
        int insertedCategory,
        int insertedItem,
        int updatedCategory,
        int updatedItem,
        long changed,

        String errCd,
        String creBy,
        LocalDateTime creAt
) {
    public static G2bSyncHistory success(SyncResult r, String actor) {
        return new G2bSyncHistory(
                null, true,
                r.begin(), r.end(),
                r.fetched(), r.deduped(), r.duplicated(),
                r.insertedCategory(), r.insertedItem(),
                r.updatedCategory(), r.updatedItem(),
                r.changed(),
                "200",
                actor,
                LocalDateTime.now()
        );
    }

    public static G2bSyncHistory fail(String begin, String end, String actor, String errCd) {
        return new G2bSyncHistory(
                null, false,
                begin, end,
                0, 0, 0,
                0, 0, 0, 0,
                0L,
                (errCd == null || errCd.isBlank()) ? "500" : errCd,
                actor,
                LocalDateTime.now()
        );
    }
}
