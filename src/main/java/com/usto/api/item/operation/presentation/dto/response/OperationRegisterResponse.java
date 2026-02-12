package com.usto.api.item.operation.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "운용 등록 결과")
public class OperationRegisterResponse {
    @Schema(description = "생성된 운용 ID (UUID)")
    private UUID operMId;
}