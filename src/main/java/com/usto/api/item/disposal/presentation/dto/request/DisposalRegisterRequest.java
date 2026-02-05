package com.usto.api.item.disposal.presentation.dto.request;

import com.usto.api.item.disposal.domain.model.DisposalArrangementType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "처분 등록 요청")
public class DisposalRegisterRequest {

    @Schema(description = "처분일자", example = "2026-02-01")
    @NotNull(message = "처분일자는 필수입니다.")
    @PastOrPresent(message = "처분일자는 미래 날짜를 선택할 수 없습니다.")
    private LocalDate dispAt;

    @Schema(description = "처분정리구분 (SALE:매각, DONATION:기증, DISCARD:폐기, TRANSFER:무상양여)", example = "SALE")
    @NotNull(message = "처분정리구분은 필수입니다.")
    private DisposalArrangementType dispType;

    @Schema(description = "처분 등록할 물품 고유번호 목록 (불용(DSU) 상태만 가능)", example = "[\"M202600001\", \"M202600002\"]")
    @NotEmpty(message = "등록할 물품을 최소 1개 이상 선택해야 합니다.")
    private List<String> itmNos;
}