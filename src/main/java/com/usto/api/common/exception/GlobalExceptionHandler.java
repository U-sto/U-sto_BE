package com.usto.api.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.usto.api.common.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @class GlobalExceptionHandler
 * @desc 프로젝트 전역 예외 처리기 - 모든 도메인의 예외를 ApiResponse 포맷으로 통일
 */
@Slf4j
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {

        String message = "잘못된 요청 형식입니다.";

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {

            String fieldName = ife.getPath().stream()
                    .map(ref -> ref.getFieldName())
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("unknown");

            message = String.format("'%s' 값이 올바르지 않습니다. 허용된 값을 확인하세요.", fieldName);
        }

        ApiResponse<Void> body = ApiResponse.fail(message);

        return ResponseEntity.badRequest().body(body);
    }
}