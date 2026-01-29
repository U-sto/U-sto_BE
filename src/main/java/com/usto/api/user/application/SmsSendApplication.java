/*
 * SmsVerificationService
 * - 역할: 휴대폰(SMS) 인증번호 발송/검증을 담당하는 서비스입니다.
 * - 정책: 4자리 숫자 코드, 유효시간 5분. (이메일은 6자리 별도 정책)
 * - 저장: Verification 엔티티를 (target=전화번호, type=SMS) 키로 관리합니다.
 */
package com.usto.api.user.application;


import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.utils.SmsUtil;
import com.usto.api.user.domain.model.Verification;
import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import com.usto.api.user.domain.repository.VerificationRepository;
import com.usto.api.user.presentation.dto.request.SmsSendRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsSendApplication {

    private final SmsUtil smsUtil; // 추가: SmsUtil 스프링 빈 주입 - 이렇게 해야 생성자 주입을 할 수가 있게 된다.
    private final VerificationRepository verificationRepository; // 인증 이력 저장/조회용 JPA 리포지토리

    @Transactional
    public void sendCodeToSms(
            SmsSendRequestDto request,
            String actor
    )
    {
        LocalDateTime timeLimit = LocalDateTime.now().plusMinutes(5);

        String code = generateRandomCode(6);

        //기존내역 확인
        Verification existingVerification  = verificationRepository
                .find(
                        request.getTarget(),
                        VerificationType.SMS,
                        request.getPurpose()
                        )
                .orElse(null); //없으면? Null 처리

        Verification verificationToSave;

        if (existingVerification  == null) { //null처리? -> 없구나 ㅇㅋ ㄱㄱ
            // 새로 생성
            verificationToSave  = Verification.builder()
                    .creBy(actor)
                    .purpose(request.getPurpose())
                    .target(request.getTarget())
                    .type(VerificationType.SMS)
                    .code(code)
                    .expiresAt(timeLimit)
                    .isVerified(false)
                    .build();
            log.info("[SMS-SEND] 새 인증 생성 - target: {}, purpose: {}",
                    request.getTarget(), request.getPurpose());
        } else {
            //재발송하는 경우
            Verification renewed = existingVerification.renew(code, timeLimit);

            verificationToSave = renewed.toBuilder()
                    .updBy(actor)
                    .build();

            log.info("[SMS-SEND] 인증 재발송 - target: {}, purpose: {}",
                    request.getTarget(), request.getPurpose());
        }
        verificationRepository.save(verificationToSave);

        // SMS 발송 (숫자만 남기도록 정규화)
        String to = request.getTarget().replaceAll("[^0-9]", "");
        try {
            SingleMessageSentResponse response = smsUtil.sendVerificationCode(to, code);

            String statusCode = response.getStatusCode();
            log.info("[SMS-SEND] statusCode={}", statusCode);

            if ("3059".equals(statusCode)) { //변작된 전화번호 오류 발생
                throw new BusinessException("재 전송 해주세요."); //타 업체 로직 추가?
            }

        } catch (Exception e) {
            // response 없이 터지는 실패까지 커버
            throw new BusinessException("재 전송 해주세요.");
        }

        log.info("[SMS-SEND] sms sent to= {}", to);
    }


    private String generateRandomCode(int Length) {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < Length; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}