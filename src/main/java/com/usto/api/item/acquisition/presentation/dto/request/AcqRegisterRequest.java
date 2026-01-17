package com.usto.api.item.acquisition.presentation.dto.request;

import com.usto.api.item.acquisition.domain.model.AcqArrangementType;
import com.usto.api.item.common.model.OperStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter @Setter @NoArgsConstructor
@Schema(description = "물품 취득 등록 요청")
public class AcqRegisterRequest {

    @Schema(description = "G2B 물품식별코드", example = "12345678")
    @NotBlank(message = "물품식별코드는 필수입니다.")
    private String g2bDCd;

    @Schema(description = "취득일자", example = "2026-01-18")
    @NotNull(message = "취득일자는 필수입니다.")
    private LocalDate acqAt;

    @Schema(description = "취득수량", example = "10")
    @NotNull(message = "취득수량은 필수입니다.")
    @Positive(message = "수량은 1개 이상이어야 합니다.")
    private Integer acqQty;

    @Schema(description = "정리구분 (BUY:자체구입, DONATE:기증, MAKE:자체제작)", example = "BUY")
    @NotNull(message = "정리구분은 필수입니다.")
    private AcqArrangementType arrgTy;

    @Schema(description = "운용상태 (ACQ:취득, OPER:운용)", example = "ACQ")
    @NotNull(message = "운용상태는 필수입니다.")
    private OperStatus operSts;

    @Schema(description = "운용부서코드", example = "ADM_FAC")
    private String deptCd;

    @Schema(description = "비고")
    private String rmk;
}