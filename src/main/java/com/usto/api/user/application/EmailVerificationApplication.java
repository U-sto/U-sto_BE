/*
 * EmailVerificationService
 * - 역할: 이메일 인증번호 발송/검증과 만료 데이터 정리를 담당합니다.
 * - 정책: 6자리 숫자 코드, 유효시간 5분, 검증 성공 시 isVerified=true로 마킹합니다.
 * - 저장: Verification 엔티티를 (target=email, type=EMAIL) 키로 관리합니다.
 */
package com.usto.api.user.application;

import com.usto.api.user.domain.model.Verification;
import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import com.usto.api.user.domain.repository.VerificationRepository;
import com.usto.api.user.presentation.dto.request.EmailVerifyRequestDto;
import com.usto.api.user.presentation.dto.request.SmsVerifyRequestDto;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationApplication {

    private final VerificationRepository verificationRepository; // 인증 이력 저장/조회용 JPA 리포지토리


    @org.springframework.transaction.annotation.Transactional
    public void verifyCode(EmailVerifyRequestDto request) {
        LocalDateTime now = LocalDateTime.now();
        Verification verification = verificationRepository
                .find(
                        request.getTarget(),
                        request.getType(),
                        request.getPurpose())

                .orElseThrow(() -> new IllegalArgumentException("인증요청이 없습니다."));


        // 1. 시간 만료 체크
        if (verification.getExpiresAt().isBefore(now)) {
            throw new IllegalArgumentException("인증 시간이 만료되었습니다. 다시 요청해주세요.");
        }

        // 2. 코드 일치 체크
        if (!verification.getCode().equals(request.getCode())) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        // 3. 인증 완료 처리
        verification.verify(now);

        // 4. 저장
        verificationRepository.save(verification);
    }

    /**
     * 매일 자정에 만료된 인증 데이터를 정리 (용량 감안) - 선택
     * cron = "초 분 시 일 월 요일"
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteExpiredVerificationCodes() {
        int deleted = verificationRepository.deleteExpiredBefore(LocalDateTime.now());
        log.info("Expired verification deleted. deletedCount={}", deleted);
    }
}
