package com.usto.api.common.exception;

import com.usto.api.common.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
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
    public ApiResponse<?> handleLoginFailed(LoginFailedException e) {
        return switch (e.getReason()) {
            case INVALID_CREDENTIALS -> ApiResponse.fail("아이디 또는 비밀번호가 올바르지 않습니다.");
            case NOT_APPROVED -> ApiResponse.fail("승인 대기 중입니다.");
            case DELETED -> ApiResponse.fail("탈퇴한 회원입니다.");
            };
    }

    /**
     *  Spring Security 기본 로그인 실패(아이디/비번 틀림)
     * - authenticate()에서 BadCredentialsException이 올라옴
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleBadCredentials(BadCredentialsException e) {
        return ApiResponse.fail("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    /**
     * 기타 인증 예외(필요 시 메시지 통일)
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleAuth(AuthenticationException e) {
        return ApiResponse.fail("로그인에 실패했습니다."); //서버 문제
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
}