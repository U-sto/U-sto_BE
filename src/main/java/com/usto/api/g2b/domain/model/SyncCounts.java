package com.usto.api.g2b.domain.model;

public record SyncCounts(
        int newCategoryCnt,
        int changedCategoryCnt,
        int newItemCnt,
        int changedItemCnt
){}
