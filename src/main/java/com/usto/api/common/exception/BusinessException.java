package com.usto.api.common.exception;

import lombok.Getter;

/**
 * @class BusinessException
 * @desc 프로젝트 모든 도메인에서 공통으로 사용할 비즈니스 예외의 부모 클래스
 */
@Getter
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}