package com.usto.api.user.domain.model;

public enum VerificationPurpose {
    SIGNUP,
    FIND_ID,
    RESET_PASSWORD;

    public static VerificationPurpose determinePurpose(String usrId, String usrNm) {
        boolean hasUsrId = usrId != null && !usrId.trim().isEmpty();
        boolean hasUsrNm = usrNm != null && !usrNm.trim().isEmpty();

        // 둘 다 없음 → 회원가입
        if (!hasUsrId && !hasUsrNm) {
            return VerificationPurpose.SIGNUP;
        }

        // usrNm만 있음 → 아이디 찾기
        if (!hasUsrId && hasUsrNm) {
            return VerificationPurpose.FIND_ID;
        }

        // usrId만 있음 → 비밀번호 찾기
        if (hasUsrId && !hasUsrNm) {
            return VerificationPurpose.RESET_PASSWORD;
        }

        // 둘 다 있음 → 잘못된 요청
        throw new IllegalArgumentException(
                "usrId와 usrNm 중 하나만 입력해주세요. " +
                        "(아이디 찾기: usrNm만, 비밀번호 찾기: usrId만, 회원가입: 둘 다 없음)"
        );
    }
}
