package com.usto.api.item.disposal.domain.model;

import com.usto.api.common.code.domain.CodeGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 처분 정리구분 Enum
 */
@Getter
@AllArgsConstructor
public enum DisposalArrangementType implements CodeGroup {

    DISCARD("폐기"),
    SALE("매각"),
    LOSS("멸실"),
    THEFT("도난");

    private final String description;

    @Override
    public String getCode() {
        return this.name();
    }
}