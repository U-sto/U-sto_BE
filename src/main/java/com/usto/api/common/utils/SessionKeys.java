package com.usto.api.common.utils;

public final class SessionKeys {
    private SessionKeys() {}

    // ===== 중복 확인 =====
    public static final String EXISTS_EMAIL_CHECKED = "exists.auth.email.exists";
    public static final String EXISTS_EMAIL_TARGET = "exists.auth.email.target";
    public static final String EXISTS_SMS_CHECKED = "exists.auth.sms.exists";
    public static final String EXISTS_SMS_TARGET = "exists.auth.sms.target";
    public static final String EXISTS_USER_ID_CHECKED = "exists.auth.usrId.exists";
    public static final String EXISTS_USER_ID_TARGET = "exists.auth.usrId.target";

    // ===== 이메일 인증 대기 =====
    public static final String EMAIL_PENDING_PURPOSE = "email.pending.purpose";
    public static final String EMAIL_PENDING_TARGET = "email.pending.target";
    public static final String EMAIL_PENDING_SENT_AT = "email.pending.sentAt";
    public static final String EMAIL_PENDING_USER_NAME = "email.pending.usrNm";
    public static final String EMAIL_PENDING_USER_ID = "email.pending.usrId";

    // ===== SMS 인증 대기 =====
    public static final String SMS_PENDING_PURPOSE = "sms.pending.purpose";
    public static final String SMS_PENDING_TARGET = "sms.pending.target";
    public static final String SMS_PENDING_SENT_AT = "sms.pending.sentAt";

    // ===== 회원가입 인증 완료 =====
    public static final String SIGNUP_AUTH_EMAIL = "signup.auth.email";
    public static final String SIGNUP_AUTH_SMS = "signup.auth.sms";
    public static final String SIGNUP_AUTH_EXPIRES_AT = "signup.auth.expiresAt";

    // ===== 아이디 찾기 인증 완료 =====
    public static final String FIND_ID_AUTH_USER_NAME = "findId.auth.usrNm";
    public static final String FIND_ID_AUTH_EMAIL = "findId.auth.email";
    public static final String FIND_ID_AUTH_EXPIRES_AT = "findId.auth.expiresAt";

    // ===== 비밀번호 재설정 인증 완료 =====
    public static final String RESET_PWD_AUTH_USER_ID = "resetPwd.auth.usrId";
    public static final String RESET_PWD_AUTH_EMAIL = "resetPwd.auth.email";
    public static final String RESET_PWD_AUTH_SMS = "resetPwd.auth.sms";
    public static final String RESET_PWD_AUTH_EXPIRES_AT = "resetPwd.auth.expiresAt";
}
