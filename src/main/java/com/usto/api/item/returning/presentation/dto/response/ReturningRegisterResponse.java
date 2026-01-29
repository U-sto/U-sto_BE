package com.usto.api.item.returning.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "반납 등록 결과")
public class ReturningRegisterResponse {
    @Schema(description = "생성된 반납 ID (UUID)")
    private UUID rtrnMId;
}