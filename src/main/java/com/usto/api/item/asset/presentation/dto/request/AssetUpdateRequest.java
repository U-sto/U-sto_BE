package com.usto.api.item.asset.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "개별 물품 정보 수정 요청")
public class AssetUpdateRequest {

    @Schema(description = "취득단가", example = "1500000")
    @NotNull(message = "취득단가는 필수입니다.")
    @DecimalMin(value = "0", message = "취득단가는 0원 이상이어야 합니다.")
    private BigDecimal acqUpr;

    @Schema(description = "내용연수", example = "5년")
    @NotNull(message = "내용연수는 필수입니다.")
    private String drbYr;

    @Schema(description = "비고", example = "2024년 구매품")
    private String rmk;
}