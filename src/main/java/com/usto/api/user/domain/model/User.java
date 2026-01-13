package com.usto.api.user.domain.model;

import com.usto.api.common.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
@Getter
public class User extends BaseTime {

    private String usrId;
    private String usrNm;
    private String pwHash;
    private String email;
    private String sms;
    private Role roleId;
    private String orgCd;
    private ApprovalStatus apprSts;
    private boolean delYn;
    private LocalDateTime delAt;

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 비즈니스 규칙 (도메인 로직)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━


    public boolean canLogin() {
        return this.roleId != Role.GUEST
                && this.apprSts == ApprovalStatus.APPROVED;
    }

    public boolean isAdmin() {
        return this.roleId == Role.ADMIN;
    }

    public boolean isManager() {
        return this.roleId == Role.MANAGER;
    }

    public boolean isWaitingApproval() {
        return this.apprSts == ApprovalStatus.WAIT;
    }

    public boolean isApproved() {
        return this.apprSts == ApprovalStatus. APPROVED;
    }

    public User approve(String approverUsrId, Role newRole) {
        if (this.apprSts == ApprovalStatus.APPROVED) {
            throw new IllegalStateException("이미 승인된 사용자입니다");
        }
        if (newRole == Role.GUEST) {
            throw new IllegalArgumentException("GUEST 역할로는 승인할 수 없습니다");
        }

        return this.toBuilder()
                .apprSts(ApprovalStatus.APPROVED)
                .roleId(newRole)
                .build();
    }

    public User updateProfile(String usrNm, String email, String sms ) {
        return this.toBuilder()
                .usrNm(usrNm != null && !usrNm.equals("사용자이름") ?
                        usrNm : this.usrNm)
                .email(email != null && !email.equals("usto@example.com") ?
                                email : this.email)
                .sms(sms != null && !sms.equals("01000000000") ?
                        sms : this.sms)
                .build();
    }

    public User changePassword(String newPwHash) {
        if (newPwHash == null || newPwHash.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다");
        }
        return this.toBuilder()
                .pwHash(newPwHash)
                .build();
    }

}