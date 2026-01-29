package com.usto.api.item.returning.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 반납사유 Enum
 */
@Getter
@AllArgsConstructor
public enum ReturningReason {
    USAGE_PERIOD_EXPIRED("사용연한경과"),
    BROKEN("고장/파손"),
    DISUSE_DECISION("불용결정"),
    PROJECT_ENDED("사업종료"),
    SURPLUS("잉여물품");

    private final String description;
}