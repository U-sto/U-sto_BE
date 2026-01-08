package com.usto.api.gemini.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @class GeminiResponseDto
 * @desc Gemini AI 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Gemini AI 응답")
public class GeminiResponseDto {
    
    @Schema(description = "AI 응답 내용", example = "U-sto는 공공조달을 위한 시스템입니다...")
    private String response;
}
