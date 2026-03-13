package com.usto.api.item.acquisition.domain.model;

import com.usto.api.common.code.domain.CodeGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AcqArrangementType implements CodeGroup {
    BUY("자체구입"),
    DONATE("기증"),
    MAKE("자체제작");

    private final String description;

    @Override
    public String getCode() {
        return this.name();
    }
}