package com.usto.api.g2b.presentation.dto.response;

import com.usto.api.g2b.domain.model.SyncCounts;

//만들어두긴 했는데 굳이 사용할 지는 고민 중입니다.
public record G2bSyncResultResponse(
        int newItemCnt,
        int changedItemCnt,
        int newCategoryCnt,
        int changedCategoryCnt
) {
    public static G2bSyncResultResponse from(SyncCounts c) {
        return new G2bSyncResultResponse(
                c.newItemCnt(),
                c.changedItemCnt(),
                c.newCategoryCnt(),
                c.changedCategoryCnt()
        );
    }
}
