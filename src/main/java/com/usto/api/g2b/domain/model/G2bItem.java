package com.usto.api.g2b.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 조달청 세부 품목 도메인 모델
 */
@Getter
@Builder
public class G2bItem {
    private final String g2bDCd;
    private final String g2bMCd;
    private final String g2bDNm;
    private final BigDecimal g2bUpr;
}