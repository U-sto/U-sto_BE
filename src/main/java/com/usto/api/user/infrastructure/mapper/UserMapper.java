package com.usto.api.user.infrastructure.mapper;

import com.usto.api.user.domain.model.User;
import com.usto.api.user.infrastructure.entity.UserJpaEntity;

public final class UserMapper {
    private UserMapper() {}

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
                .delYn(u.isDelYn())
                .delAt(u.getDelAt())
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
                .delYn(e.isDelYn())
                .delAt(e.getDelAt())
                // base time
                .creBy(e.getCreBy())
                .creAt(e.getCreAt())
                .updBy(e.getUpdBy())
                .updAt(e.getUpdAt())
                .build();
    }
}
