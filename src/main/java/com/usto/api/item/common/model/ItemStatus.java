package com.usto.api.item.common.model;

import com.usto.api.common.code.domain.CodeGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemStatus implements CodeGroup {
    NEW("신품"),
    USED("중고품"),
    REPAIRABLE("요정비품"),
    SCRAP("폐품");

    private final String description;

    @Override
    public String getCode() {
        return this.name();
    }
}