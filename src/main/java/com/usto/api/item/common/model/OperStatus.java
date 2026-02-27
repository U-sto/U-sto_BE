package com.usto.api.item.common.model;

import com.usto.api.common.code.domain.CodeGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperStatus implements CodeGroup {
    OPER("운용"),
    RTN("반납"),
    DSU("불용"),
    DISP("처분");

    private final String description;

    @Override
    public String getCode() {
        return this.name();
    }
}