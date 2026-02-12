package com.usto.api.ai.common;

public class AiCallException extends RuntimeException {
    private final String code;

    public AiCallException(String code, String message) {
        super(message);
        this.code = code;
    }

    public AiCallException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String code() { return code; }
}
