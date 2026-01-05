package com.usto.api.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 예외 발생 시 클라이언트에게 반환되는 표준화된 에러 응답 포맷
 * - code: 에러 코드 (예: LOGIN_FAILED)
 * - message: 사용자에게 보여질 에러 메시지
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private final String code;
    private final String message;

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }
}
