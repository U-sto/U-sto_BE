package com.usto.api.item.disuse.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "불용 등록 결과")
public class DisuseRegisterResponse {
    @Schema(description = "생성된 불용 ID (UUID)")
    private UUID dsuMId;
}