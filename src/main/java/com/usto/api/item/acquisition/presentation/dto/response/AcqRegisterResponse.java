package com.usto.api.item.acquisition.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "물품 취득 등록 결과")
public class AcqRegisterResponse {
    @Schema(description = "생성된 취득 ID")
    private String acqId;
}