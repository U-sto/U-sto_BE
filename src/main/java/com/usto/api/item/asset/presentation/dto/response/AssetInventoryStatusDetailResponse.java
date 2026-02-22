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
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "물품보유현황 상세정보 응답")
public class AssetInventoryStatusDetailResponse {

    @Schema(description = "물품고유번호 목록 (이 그룹에 속한 모든 물품)")
    private List<String> itmNos;

    @Schema(description = "G2B 목록명")
    private String g2bDNm;

    @Schema(description = "G2B 목록번호 (분류-식별)", example = "12345678-12345678")
    private String g2bItemNo;

    @Schema(description = "취득일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate acqAt;

    @Schema(description = "정리일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate arrgAt;

    @Schema(description = "운용상태")
    private OperStatus operSts;

    @Schema(description = "내용연수")
    private String drbYr;

    @Schema(description = "취득금액")
    private BigDecimal acqUpr;

    @Schema(description = "수량")
    private Integer qty;

    @Schema(description = "취득정리구분")
    private String acqArrgTy;

    @Schema(description = "운용부서명")
    private String deptNm;

    @Schema(description = "운용부서코드")
    private String deptCd;

    @Schema(description = "비고")
    private String rmk;
}