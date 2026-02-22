package com.usto.api.item.disposal.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 처분 정리구분 Enum
 */
@Getter
@AllArgsConstructor
public enum DisposalArrangementType {

    DISCARD("폐기"),
    SALE("매각"),
    LOSS("멸실"),
    THEFT("도난");

    @JsonValue
    private final String description;
}