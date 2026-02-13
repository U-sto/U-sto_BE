package com.usto.api.item.operation.presentation.dto.request;

import com.usto.api.item.common.model.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "운용 등록 요청")
public class OperationRegisterRequest {

    @Schema(description = "운용일자", example = "2026-01-28")
    @NotNull(message = "운용일자는 필수입니다.")
    @PastOrPresent(message = "운용일자는 미래 날짜를 선택할 수 없습니다.")
    private LocalDate aplyAt;

    @Schema(description = "물품상태 (NEW:신품, USED:중고품, REPAIRABLE:요정비품, SCRAP:폐품)", example = "USED")
    @NotNull(message = "물품상태는 필수입니다.")
    private ItemStatus itemSts;

    @Schema(description = "운용부서코드", example = "A001")
    @NotBlank(message = "운용부서코드는 필수입니다.")
    private String deptCd;

    @Schema(description = "운용할 물품 고유번호 목록", example = "[\"M202600001\", \"M202600002\"]")
    @NotEmpty(message = "물품은 최소 1개 이상 선택해야 합니다.")
    private List<String> itmNos;
}