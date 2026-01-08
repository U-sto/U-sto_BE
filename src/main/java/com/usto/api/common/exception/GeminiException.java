package com.usto.api.common.exception;

/**
 * @class GeminiException
 * @desc Gemini AI 관련 예외 클래스
 */
public class GeminiException extends RuntimeException {
    
    public GeminiException(String message) {
        super(message);
    }
    
    public GeminiException(String message, Throwable cause) {
        super(message, cause);
    }
}
