package com.usto.api.gemini.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @class GeminiRequestDto
 * @desc Gemini AI 질문 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Gemini AI 질문 요청")
public class GeminiRequestDto {
    
    @Schema(description = "사용자 질문", example = "U-sto 시스템에 대해 설명해주세요.")
    @NotBlank(message = "질문을 입력해주세요.")
    private String prompt;
}
