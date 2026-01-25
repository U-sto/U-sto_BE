package com.usto.api.item.asset.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AssetDetailRow {
    private String itmNo;

    private String g2bDNm;
    private String g2bMCd;
    private String g2bDCd;

    public String getG2bListNo() {
        if (g2bMCd == null || g2bDCd == null) return null;
        return g2bMCd + "-" + g2bDCd;
    }

    private LocalDate acqAt;
    private LocalDate arrgAt;

    private String operSts;
    private String drbYr;
    private BigDecimal acqUpr;

    //최신 수량(현재 수량)
    private Integer currentQty;

    //최초 수량(취득 수량)
    private Integer acquisitionQty;

    private String acqArrgTy;

    private String deptCd;
    private String rmk;
}
