package com.usto.api.item.returning.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 반납사유 Enum
 */
@Getter
@AllArgsConstructor
public enum ReturningReason {
    PROJECT_ENDED("사업종료"),
    SURPLUS("잉여물품"),
    COMMON_CONVERSION("공용전환"),
    BROKEN("파손");

    @JsonValue
    private final String description;
}