package com.usto.api.item.common.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemStatus {
    NEW("신품"),
    USED("중고품"),
    REPAIRABLE("요정비품"),
    SCRAP("폐품");

    @JsonValue
    private final String description;
}