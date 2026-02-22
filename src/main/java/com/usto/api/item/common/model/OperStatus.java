package com.usto.api.item.common.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperStatus {
    OPER("운용"),
    RTN("반납"),
    DSU("불용"),
    DISP("처분");

    @JsonValue
    private final String description;
}