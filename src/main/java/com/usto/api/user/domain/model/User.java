package com.usto.api.user.domain.model;

import com.usto.api.common.BaseTime;
import com.usto.api.common.exception.BusinessException;
import lombok.Getter;
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
    private String apprUsrId;
    private ApprovalStatus apprSts;
    private LocalDateTime apprAt;
    private boolean delYn;
    private LocalDateTime delAt;

    public boolean canLogin() {
        return this.roleId != Role.GUEST
                && this.apprSts == ApprovalStatus.APPROVED;
    }

    public User approve(Role assignedRole,String apprUsrId) {
        if (this.apprSts == ApprovalStatus.APPROVED) {
            throw new IllegalStateException("이미 승인된 사용자입니다");
        }
        if(this.apprSts == ApprovalStatus.REJECTED){
            throw new IllegalStateException("이미 반려된 사용자입니다");
        }
        if (assignedRole == Role.GUEST) {
            throw new IllegalArgumentException("GUEST 역할로는 승인할 수 없습니다");
        }

        return this.toBuilder()
                .apprUsrId(apprUsrId)
                .apprSts(ApprovalStatus.APPROVED)
                .apprAt(LocalDateTime.now())
                .roleId(assignedRole)
                .build();
    }

    public User reject(String apprUsrId) {
        if (this.apprSts == ApprovalStatus.APPROVED) {
            throw new IllegalStateException("이미 승인된 사용자입니다");
        }
        if (this.getApprSts() == ApprovalStatus.REJECTED) {
            throw new BusinessException("이미 반려된 회원입니다.");
        }

        return this.toBuilder()
                .apprUsrId(apprUsrId)
                .apprSts(ApprovalStatus.REJECTED)
                .apprAt(LocalDateTime.now())
                .build();
    }

    public User changeSms(String sms ) {
        return this.toBuilder()
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