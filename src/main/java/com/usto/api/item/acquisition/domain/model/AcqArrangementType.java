package com.usto.api.item.acquisition.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AcqArrangementType {
    BUY("자체구입"),
    DONATE("기증"),
    MAKE("자체제작");

    private final String description;
}