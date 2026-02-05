package com.usto.api.item.disposal.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "처분 등록 결과")
public class DisposalRegisterResponse {

    @Schema(description = "생성된 처분 ID (UUID)")
    private UUID dispMId;
}