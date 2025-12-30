/*
 * SmsVerificationService
 * - 역할: 휴대폰(SMS) 인증번호 발송/검증을 담당하는 서비스입니다.
 * - 정책: 4자리 숫자 코드, 유효시간 5분. (이메일은 6자리 별도 정책)
 * - 저장: Verification 엔티티를 (target=전화번호, type=PHONE) 키로 관리합니다.
 */
package com.usto.api.user.application;


import com.usto.api.common.utils.SmsUtil;
import com.usto.api.user.domain.model.Verification;
import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import com.usto.api.user.domain.repository.VerificationRepository;
import com.usto.api.user.infrastructure.entity.VerificationJpaEntity;
import com.usto.api.user.infrastructure.repository.VerificationJpaRepository;
import com.usto.api.user.presentation.dto.request.SmsSendRequestDto;
import com.usto.api.user.presentation.dto.request.SmsVerifyRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SmsSendApplication {

    private final SmsUtil smsUtil; // 추가: SmsUtil 스프링 빈 주입 - 이렇게 해야 생성자 주입을 할 수가 있게 된다.
    private final VerificationRepository verificationRepository; // 인증 이력 저장/조회용 JPA 리포지토리

    /**
     * 인증번호 발송
     * @param request
     */
    @Transactional
    public void sendCodeToSms(SmsSendRequestDto request,String actor) {

        // 0. 시간 제한 생성
        LocalDateTime timeLimit = LocalDateTime.now().plusMinutes(5);

        // 1. 인증번호 생성 (1000 ~ 9999)
        String code = generateRandomCode();

        // 2. 기존 내역 확인 (있으면 갱신, 없으면 생성)
        Verification verification = verificationRepository
                .find(
                        request.getTarget(),
                        request.getType(),
                        request.getPurpose()
                        )
                .orElse(null); //없으면? Null 처리

        if (verification == null) { //null처리? -> 없구나
            // 새로 생성
            verification = Verification.builder()
                    .creBy(actor)
                    .purpose(request.getPurpose())
                    .target(request.getTarget())
                    .type(request.getType())
                    .code(code)
                    .expiresAt(timeLimit) // 5분 제한
                    .isVerified(false)
                    .build();
            verificationRepository.save(verification);
        } else {
            // 기존 내역이 있으면 재전송
            verification.renew(code,timeLimit);
            verification.setUpdBy(actor); // 갱신자 남기기
        }
        verificationRepository.save(verification); // ← 두 분기 공통 저장

        // 3. SMS 발송 (숫자만 남기도록 정규화)
        String to = request.getTarget().replaceAll("[^0-9]", "");
        smsUtil.sendVerificationCode(to, code);
    }

    /**
     * 4자리 랜덤 숫자 코드를 생성
     */
    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}