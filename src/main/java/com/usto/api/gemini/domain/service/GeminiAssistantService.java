package com.usto.api.gemini.domain.service;

/**
 * @interface GeminiAssistantService
 * @desc Gemini AI 어시스턴트 서비스 인터페이스
 */
public interface GeminiAssistantService {
    /**
     * Gemini AI에 질문을 전송하고 응답을 받습니다.
     *
     * @param prompt 사용자 질문
     * @return AI 응답
     */
    String generateResponse(String prompt);
}
