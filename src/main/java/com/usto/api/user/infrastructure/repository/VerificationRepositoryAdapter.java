package com.usto.api.user.infrastructure.repository;

import com.usto.api.user.domain.model.Verification;
import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import com.usto.api.user.domain.repository.VerificationRepository;
import com.usto.api.user.infrastructure.entity.VerificationJpaEntity;
import com.usto.api.user.infrastructure.entity.VerificationJpaEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/*
    Repository와 JpaRepository를 연결하는 어댑터
    Application은 Repository만 사용하지만, 실제 DB 접근은 JpaRepository가 하나기때문에 필요함
 */

@Repository
@RequiredArgsConstructor //@RequiredArgsConstructor는 final 필드만 생성자에 넣어줌
public class VerificationRepositoryAdapter implements VerificationRepository {

    private final VerificationJpaRepository verificationJpaRepository;

    @Override
    public Optional<Verification> find(
            String target,
            VerificationType type,
            VerificationPurpose purpose) {
        return verificationJpaRepository.findByTypeAndPurposeAndTarget(
                        type,
                        purpose,
                        target)
                .map(VerificationJpaEntityMapper::toDomain);
    }

    @Override
    public Verification save(
            Verification verification) {
        VerificationJpaEntity mapped = VerificationJpaEntityMapper.toEntity(verification);
        VerificationJpaEntity saved = verificationJpaRepository.save(mapped);
        return VerificationJpaEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(
            String target,
            VerificationType type,
            VerificationPurpose purpose) {
        verificationJpaRepository.findByTypeAndPurposeAndTarget(
                        type,
                        purpose,
                        target)
                .ifPresent(verificationJpaRepository::delete);
    }

    @Override
    public int deleteExpiredBefore(LocalDateTime now) {
        return verificationJpaRepository.deleteByExpiresAtBefore(now);
    }
}