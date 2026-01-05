package com.usto.api.common.exception;

//아.. 다른 애들도 예외처리를 해줘야겠구나..
public class LoginFailedException extends RuntimeException {
    public LoginFailedException() {
        super("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
}
