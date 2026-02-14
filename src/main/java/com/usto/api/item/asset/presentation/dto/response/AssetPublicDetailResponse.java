package com.usto.api.item.asset.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Schema(description = "물품 공개 정보 응답 (QR 스캔용 - 최신 데이터)")
public class AssetPublicDetailResponse {

    @Schema(description = "물품고유번호", example = "M202600001")
    private String itmNo;

    @Schema(description = "G2B 품목명")
    private String g2bDNm;

    @Schema(description = "G2B 목록번호", example = "12345678-87654321")
    private String g2bItemNo;

    @Schema(description = "취득금액")
    private BigDecimal acqUpr;

    @Schema(description = "취득일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate acqAt;

    @Schema(description = "정리일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate arrgAt;

    @Schema(description = "운용상태", example = "운용중")
    private OperStatus operSts;

    @Schema(description = "내용연수", example = "5")
    private String drbYr;

    @Schema(description = "운용부서명")
    private String deptNm;

    @Schema(description = "조직명")
    private String orgNm;

    @Schema(description = "비고")
    private String rmk;

    @Schema(description = "현재 수량")
    private Long qty;

    // JPQL Constructor
    public AssetPublicDetailResponse(
            String itmNo,
            String orgNm,
            String g2bDNm,
            String g2bItemNo,
            BigDecimal acqUpr,
            LocalDate acqAt,
            LocalDate arrgAt,
            OperStatus operSts,
            String drbYr,
            String deptNm,
            Long qty,
            String rmk
    ) {
        this.itmNo = itmNo;
        this.orgNm = orgNm;
        this.g2bDNm = g2bDNm;
        this.g2bItemNo = g2bItemNo;
        this.acqUpr = acqUpr;
        this.acqAt = acqAt;
        this.arrgAt = arrgAt;
        this.operSts = operSts;
        this.drbYr = drbYr;
        this.deptNm = deptNm;
        this.qty = qty;
        this.rmk = rmk;
    }
}
