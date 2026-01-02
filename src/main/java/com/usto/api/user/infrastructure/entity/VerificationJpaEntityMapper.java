package com.usto.api.user.infrastructure.entity;

import com.usto.api.user.domain.model.Verification;

//이상하게 에러나는데 이유를 못 찾아서 나중에 연결하겠습니다.
public final class VerificationJpaEntityMapper {
    private VerificationJpaEntityMapper() {}

    private static Verification toDomain(VerificationJpaEntity e) {
        return Verification.builder()
                .id(e.getId())
                .type(e.getType())
                .purpose(e.getPurpose())
                .target(e.getTarget())
                .code(e.getCode())
                .expiresAt(e.getExpiresAt())
                .isVerified(e.isVerified())
                .verifiedAt(e.getVerifiedAt())
                // base time
                .creBy(e.getCreBy())
                .creAt(e.getCreAt())
                .updBy(e.getUpdBy())
                .updAt(e.getUpdAt())
                .build();
    }

    private static VerificationJpaEntity toEntity(Verification d) {
        return VerificationJpaEntity.builder()
                .id(d.getId())
                .type(d.getType())
                .purpose(d.getPurpose())
                .target(d.getTarget())
                .code(d.getCode())
                .expiresAt(d.getExpiresAt())
                .isVerified(d.isVerified())
                .verifiedAt(d.getVerifiedAt())
                // base time
                .creBy(d.getCreBy())
                .creAt(d.getCreAt())
                .updBy(d.getUpdBy())
                .updAt(d.getUpdAt())
                .build();
    }
}
