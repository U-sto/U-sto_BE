package com.usto.api.common.exception;

public class UserNotFoundException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "사용자를 찾을 수 없습니다.";

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
