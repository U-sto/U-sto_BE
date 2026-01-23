package com.usto.api.g2b.domain.model;

import lombok.Builder;
import lombok.Getter;
import com.usto.api.g2b.domain.model.G2bStg;

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

    public static G2bItem from(G2bStg stg) {
        return G2bItem.builder()
                .g2bDCd(stg.getG2bDCd())
                .g2bMCd(stg.getG2bMCd())
                .g2bDNm(stg.getG2bDNm())
                .g2bUpr(BigDecimal.valueOf(stg.getG2bUpr()))
                .build();
    }
}