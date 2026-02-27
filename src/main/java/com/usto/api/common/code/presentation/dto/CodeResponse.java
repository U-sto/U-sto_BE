package com.usto.api.common.code.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "공통코드 응답")
public class CodeResponse {

    @Schema(description = "코드 값", example = "WAIT")
    private String code;

    @Schema(description = "코드 설명", example = "작성중")
    private String label;
}