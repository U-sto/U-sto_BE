package com.usto.api.item.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprStatus {
    WAIT("대기"),
    APPROVED("확정"),
    REJECTED("반려");

    private final String description;
}