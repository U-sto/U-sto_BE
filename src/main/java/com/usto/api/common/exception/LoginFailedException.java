package com.usto.api.common.exception;

/**
 * 로그인 실패 시 발생하는 예외
 * 아이디 또는 비밀번호가 올바르지 않을 때 사용됩니다.
 */
public class LoginFailedException extends BusinessException {
    
    private static final String ERROR_CODE = "LOGIN_FAILED";
    private static final String ERROR_MESSAGE = "아이디 또는 비밀번호가 올바르지 않습니다.";
    
    public LoginFailedException() {
        super(ERROR_CODE, ERROR_MESSAGE);
    }
}
