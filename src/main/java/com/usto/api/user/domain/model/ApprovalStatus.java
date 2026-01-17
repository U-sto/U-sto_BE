package com.usto.api.user.domain.model;

public enum ApprovalStatus {
    WAIT,   // 대기
    APPROVED,   //승인 (로그인 허용)
    REJECTED; // 반려

    public boolean isApproved() {
        return this == APPROVED;
    }
}
