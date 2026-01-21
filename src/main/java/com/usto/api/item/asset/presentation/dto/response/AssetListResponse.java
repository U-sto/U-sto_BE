package com.usto.api.item.asset.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Schema(description = "운용대장 목록 응답")
public class AssetListResponse {

    @Schema(description = "물품고유번호", example = "M202600001")
    private String itmNo;

    @Schema(description = "G2B 물품번호 (분류-식별)", example = "12345678-12345678")
    private String g2bItemNo;

    @Schema(description = "G2B 물품명")
    private String g2bItemNm;

    @Schema(description = "취득일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate acqAt;

    @Schema(description = "취득금액")
    private BigDecimal acqUpr;

    @Schema(description = "정리일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate arrgAt;

    @Schema(description = "운용부서명")
    private String deptNm;

    @Schema(description = "운용상태")
    private String operSts;

    @Schema(description = "내용연수")
    private String drbYr;
}