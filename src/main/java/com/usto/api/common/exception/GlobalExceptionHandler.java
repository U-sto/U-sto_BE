package com.usto.api.common.exception;

import com.usto.api.common.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @class GlobalExceptionHandler
 * @desc 프로젝트 전역 예외 처리기 - 모든 도메인의 예외를 ApiResponse 포맷으로 통일
 */
@RestControllerAdvice(basePackages = "com.usto.api")
public class GlobalExceptionHandler {
    /**
     * BusinessException 클래스를 상속받은 모든 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        return ApiResponse.fail(e.getMessage());
    }

    /**
     * 로그인 실패 예외 처리
     */
    @ExceptionHandler(LoginFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleLoginFailedException(LoginFailedException e) {
        return ApiResponse.fail(e.getMessage());
    }

    /**
     * Bean Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ApiResponse.fail("입력값 검증에 실패했습니다.", errors);
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleGeneralException(Exception e) {
        return ApiResponse.fail("서버 오류가 발생했습니다.");
    }
}