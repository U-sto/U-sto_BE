package com.usto.api.user.infrastructure.entity;

import com.usto.api.user.domain.model.ApprovalStatus;
import com.usto.api.user.domain.model.Role;
import com.usto.api.user.domain.model.User;

// 다시 생각해보니 verification도 이렇게 하는게 나아보이네요. 나중에 리팩토링 하겠습니다.
public final class UserJpaEntityMapper {
    private UserJpaEntityMapper() {}

    public static UserJpaEntity toEntity(User u) {
        return UserJpaEntity.builder()
                .usrId(u.getUsrId())
                .usrNm(u.getUsrNm())
                .email(u.getEmail())
                .sms(u.getSms())
                .pwHash(u.getPwHash())
                .orgCd(u.getOrgCd())
                .apprSts(u.getApprSts())
                .roleId(u.getRoleId())
                .delYn(false)
                .delAt(null)
                // base time
                .creBy(u.getCreBy())
                .creAt(u.getCreAt())
                .updBy(u.getUpdBy())
                .updAt(u.getUpdAt())
                .build();
    }

    public static User toDomain(UserJpaEntity e) {
        return User.builder()
                .usrId(e.getUsrId())
                .usrNm(e.getUsrNm())
                .email(e.getEmail())
                .sms(e.getSms())
                .pwHash(e.getPwHash())
                .orgCd(e.getOrgCd())
                .apprSts(e.getApprSts())
                .roleId(e.getRoleId())
                .delYn(false)
                .delAt(null)
                // base time
                .creBy(e.getCreBy())
                .creAt(e.getCreAt())
                .updBy(e.getUpdBy())
                .updAt(e.getUpdAt())
                .build();
    }
}
