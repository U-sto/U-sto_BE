package com.usto.api.user.domain.repository;

import com.usto.api.user.domain.model.Verification;
import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationRepository {

    //도메인 인터페이스 표준 확립

    Optional<Verification> find(
            String target,
            VerificationType  type,
            VerificationPurpose purpose);

    Verification save(Verification verification);

    void delete(
            String target,
            VerificationType type,
            VerificationPurpose purpose
            );

    int deleteExpiredBefore(LocalDateTime now);
}
