package com.usto.api.common.exception;

//아.. 다른 애들도 예외처리를 해줘야겠구나..
public class LoginFailedException extends RuntimeException {


    public enum Reason { INVALID_CREDENTIALS, NOT_APPROVED , DELETE }

    private final Reason reason;

    private LoginFailedException(Reason reason) {
        this.reason = reason;
    }

    public static LoginFailedException invalidCredentials() {
        return new LoginFailedException(Reason.INVALID_CREDENTIALS);
    }

    public static LoginFailedException notApproved() {
        return new LoginFailedException(Reason.NOT_APPROVED);
    }

    public static LoginFailedException deleted() {
        return new LoginFailedException(Reason.DELETE);
    }

    public Reason getReason() {
        return reason;
    }
}
