package com.usto.api.item.disuse.domain.model;

import com.usto.api.common.code.domain.CodeGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 불용사유 Enum
 */
@Getter
@AllArgsConstructor
public enum DisuseReason implements CodeGroup {
    LIFE_EXPIRED("내용연수경과"),
    OBSOLETE("구형화"),
    NO_DEPT("활용부서부재"),
    HIGH_REPAIR("수리비용과다"),
    DAMAGED("고장/파손"),
    DETERIORATED("노후화(성능저하)");

    private final String description;

    @Override
    public String getCode() {
        return this.name();
    }
}