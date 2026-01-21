package com.usto.api.g2b.domain.model;

import lombok.Builder;
import lombok.Getter;

//내부객체 - 변경을 위한 최소 정보
@Getter
@Builder
public class StgPriceRow{

    private final String g2bDCd;
    private final long g2bUpr;
}
