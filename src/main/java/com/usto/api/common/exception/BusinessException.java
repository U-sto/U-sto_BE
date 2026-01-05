package com.usto.api.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직에서 발생하는 예외의 기본 클래스
 * 모든 커스텀 예외는 이 클래스를 상속하여 에러 코드와 메시지를 제공합니다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
