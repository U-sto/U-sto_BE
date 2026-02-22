package com.usto.api.item.disuse.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 불용사유 Enum
 */
@Getter
@AllArgsConstructor
public enum DisuseReason {
    LIFE_EXPIRED("내용연수경과"),
    OBSOLETE("구형화"),
    NO_DEPT("활용부서부재"),
    HIGH_REPAIR("수리비용과다");

    @JsonValue
    private final String description;
}