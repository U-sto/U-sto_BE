package com.usto.api.common.exception;

import com.usto.api.common.utils.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.usto.api")
public class GlobalExceptionHandler {

    /**
     * @desc BusinessException을 상속받은 모든 예외를 여기서 한 번에 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        return ApiResponse.fail(e.getMessage());
    }
}