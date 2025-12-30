package com.usto.api.user.infrastructure.repository;

import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.infrastructure.entity.VerificationJpaEntity;
import com.usto.api.user.domain.model.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationJpaRepository extends JpaRepository<VerificationJpaEntity, Long> {

    // 타겟(이메일/폰)과 타입,목적으로 인증 정보 찾기
    Optional<VerificationJpaEntity> findByTypeAndPurposeAndTarget(
            VerificationType type,
            VerificationPurpose purpose,
            String target
    );

    // 만료 데이터 정리
    int deleteByExpiresAtBefore(LocalDateTime now);
}