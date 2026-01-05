package com.usto.api.common.exception.handler;

import com.usto.api.common.dto.ErrorResponse;
import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.exception.LoginFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GlobalExceptionHandler의 단위 테스트
 * 각 예외 핸들러가 올바른 에러 응답을 반환하는지 검증합니다.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    /**
     * BusinessException 처리 테스트
     */
    @Test
    void testHandleBusinessException() {
        // given
        BusinessException exception = new BusinessException("TEST_ERROR", "테스트 에러 메시지");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TEST_ERROR", response.getBody().getCode());
        assertEquals("테스트 에러 메시지", response.getBody().getMessage());
    }

    /**
     * LoginFailedException 처리 테스트
     */
    @Test
    void testHandleLoginFailedException() {
        // given
        LoginFailedException exception = new LoginFailedException();

        // when
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("LOGIN_FAILED", response.getBody().getCode());
        assertEquals("아이디 또는 비밀번호가 올바르지 않습니다.", response.getBody().getMessage());
    }

    /**
     * 일반 Exception 처리 테스트
     */
    @Test
    void testHandleGenericException() {
        // given
        Exception exception = new RuntimeException("예상치 못한 오류");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleException(exception);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getCode());
        assertEquals("서버 내부 오류가 발생했습니다.", response.getBody().getMessage());
    }

    /**
     * HttpMessageNotReadableException 처리 테스트
     */
    @Test
    void testHandleHttpMessageNotReadableException() {
        // given
        HttpMessageNotReadableException exception = 
            new HttpMessageNotReadableException("Invalid JSON", null, null);

        // when
        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(exception);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_REQUEST_BODY", response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("요청 본문을 읽을 수 없습니다"));
    }

    /**
     * HttpRequestMethodNotSupportedException 처리 테스트
     */
    @Test
    void testHandleHttpRequestMethodNotSupportedException() {
        // given
        HttpRequestMethodNotSupportedException exception = 
            new HttpRequestMethodNotSupportedException("POST");

        // when
        ResponseEntity<ErrorResponse> response = handler.handleHttpRequestMethodNotSupportedException(exception);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("METHOD_NOT_ALLOWED", response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("POST"));
    }
}
