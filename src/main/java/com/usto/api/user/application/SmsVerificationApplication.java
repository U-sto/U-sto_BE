/*
 * SmsVerificationService
 * - 역할: 휴대폰(SMS) 인증번호 발송/검증을 담당하는 서비스입니다.
 * - 정책: 4자리 숫자 코드, 유효시간 5분. (이메일은 6자리 별도 정책)
 * - 저장: Verification 엔티티를 (target=전화번호, type=SMS) 키로 관리합니다.
 */
package com.usto.api.user.application;


import com.usto.api.common.exception.BusinessException;
import com.usto.api.user.domain.model.Verification;
import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import com.usto.api.user.domain.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SmsVerificationApplication {

    private final VerificationRepository verificationRepository; // 인증 이력 저장/조회용 JPA 리포지토리

    /**
     * 인증번호 검증(문자)
     * - 저장된 Verification을 조회 → 만료시간 확인 → 코드 일치 확인 → 성공 시 verify() 호출
     */
    @Transactional
    public void verifyCode(
            String code,
            String target,
            VerificationPurpose purpose
    ) {

        if (target == null) {
            throw new BusinessException("인증번호 발송 내역이 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();

        Verification verification = verificationRepository
                .find(
                        target,
                        VerificationType.SMS,
                        purpose
                )
                .orElseThrow(() -> new IllegalArgumentException("인증요청이 없습니다."));


        // 1. 시간 만료 체크
        if (verification.getExpiresAt().isBefore(now)) {
            throw new IllegalArgumentException("인증 시간이 만료되었습니다. 다시 요청해주세요.");
        }

        // 2. 코드 일치 체크
        if (!verification.getCode().equals(code)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        // 3. 인증 완료 처리
        verification.verify(now);

        // 4. 저장
        verificationRepository.save(verification);
    }
}