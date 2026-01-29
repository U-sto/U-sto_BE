package com.usto.api.g2b.domain.model;

public record SyncResult(
        String begin,
        String end,
        int fetched,          // 외부에서 받은 총 건수
        int deduped,          // 중복 제거 후 STG 적재 건수
        int duplicated,       // 제거된 중복 건수
        int insertedCategory,
        int insertedItem,
        int updatedCategory,
        int updatedItem,
        long changed          // 최종 변경 건수(원하시는 정의로)
) {
    public static SyncResult empty(String begin, String end, int fetched) {
        return new SyncResult(begin, end, fetched, 0, 0, 0, 0, 0, 0, 0L);
    }
}
