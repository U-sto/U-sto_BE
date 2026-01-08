package com.usto.api.gemini.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.gemini.domain.service.GeminiAssistantService;
import com.usto.api.gemini.presentation.dto.GeminiRequestDto;
import com.usto.api.gemini.presentation.dto.GeminiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @class GeminiAssistantController
 * @desc Gemini AI 어시스턴트 API 컨트롤러
 */
@Tag(name = "gemini-assistant-controller", description = "Gemini AI 어시스턴트 관련 API")
@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiAssistantController {
    
    private final GeminiAssistantService geminiAssistantService;
    
    @Operation(
            summary = "Gemini AI 질문",
            description = "Gemini AI에게 질문하고 응답을 받습니다."
    )
    @PostMapping("/ask")
    public ApiResponse<GeminiResponseDto> askGemini(
            @Valid @RequestBody GeminiRequestDto requestDto) {
        String aiResponse = geminiAssistantService.generateResponse(requestDto.getPrompt());
        return ApiResponse.ok("AI 응답 생성 성공", new GeminiResponseDto(aiResponse));
    }
}
