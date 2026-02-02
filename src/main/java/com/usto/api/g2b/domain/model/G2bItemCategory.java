package com.usto.api.g2b.domain.model;

import lombok.Builder;
import lombok.Getter;
import com.usto.api.g2b.domain.model.G2bStg;

/**
 * 조달청 물품 분류 마스터 도메인 모델
 */
@Getter
@Builder
public class G2bItemCategory {
    private final String g2bMCd;
    private final String g2bMNm;
    private final String drbYr;

    public G2bItemCategory updateDrbYr(String newDrbYr) {
        return new G2bItemCategory(
                this.g2bMCd,
                this.g2bMNm,
                newDrbYr);    }
}