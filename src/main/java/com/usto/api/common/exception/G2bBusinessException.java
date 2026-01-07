package com.usto.api.common.exception;

/**
 * @class G2bBusinessException
 * @desc G2B(조달청) 검색 및 관련 비즈니스 로직 수행 중 발생하는 예외를 처리하는 클래스
 * - BusinessException을 상속받아 GlobalExceptionHandler에서 통합 관리됨
 */
public class G2bBusinessException extends BusinessException {
    public G2bBusinessException(String message) {
        super(message);
    }
}
