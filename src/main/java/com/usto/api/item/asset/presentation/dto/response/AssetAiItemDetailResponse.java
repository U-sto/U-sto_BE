package com.usto.api.item.asset.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.usto.api.item.acquisition.domain.model.AcqArrangementType;
import com.usto.api.item.common.model.OperStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 물품 특정 정보 조회 응답")
public class AssetAiItemDetailResponse {

    @Schema(description = "G2B목록명")
    private String g2bDNm;

    @Schema(description = "G2B분류번호", example = "12345678")
    private String g2bMCd;

    @Schema(description = "G2B식별번호", example = "12345678")
    private String g2bDCd;

    @Schema(description = "G2B 목록번호(분류-식별)", example = "12345678-12345678")
    public String getG2bListNo() {
        if (g2bMCd == null || g2bDCd == null) return null;
        return g2bMCd + "-" + g2bDCd;
    }

    @Schema(description = "물품고유번호", example = "M202600001")
    private String itmNo;

    @Schema(description = "취득일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate acqAt;

    @Schema(description = "취득금액")
    private BigDecimal acqUpr;

    @Schema(description = "정리일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate arrgAt;

    @Schema(description = "운용부서(명)")
    private String deptNm;

    @Schema(description = "운용부서코드")
    private String deptCd;

    @Schema(description = "운용상태")
    private OperStatus operSts;

    @Schema(description = "내용연수")
    private String drbYr;

    //최신 정보
    @Schema(description = "현재수량. 단건 조회시 1로 고정할 수도 있음")
    private Integer currentQty;

    //최초 정보
    @Schema(description = "취득수량(TB_ITEM001M.ACQ_QTY)")
    private Integer acquisitionQty;

    @Schema(description = "정리구분")
    private AcqArrangementType arrgTy;

    @Schema(description = "비고")
    private String rmk;

    //JPQL을 위한
    public AssetAiItemDetailResponse(
            String itmNo,
            String g2bDNm,
            String g2bMCd,
            String g2bDCd,
            LocalDate acqAt,
            LocalDate arrgAt,
            OperStatus operSts,
            String drbYr,
            BigDecimal acqUpr,
            Integer currentQty,
            Integer acquisitionQty,
            AcqArrangementType arrgTy,
            String deptCd,
            String rmk
    ) {
        this.itmNo = itmNo;
        this.g2bDNm = g2bDNm;
        this.g2bMCd = g2bMCd;
        this.g2bDCd = g2bDCd;
        this.acqAt = acqAt;
        this.arrgAt = arrgAt;
        this.operSts = operSts;
        this.drbYr = drbYr;
        this.acqUpr = acqUpr;
        this.currentQty = currentQty;
        this.acquisitionQty = acquisitionQty;
        this.arrgTy = arrgTy;
        this.deptCd = deptCd;
        this.rmk = rmk;
    }
}
