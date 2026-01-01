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
import com.usto.api.user.presentation.dto.request.EmailSendRequestDto;
import com.usto.api.user.presentation.dto.request.SmsSendRequestDto;
import com.usto.api.user.presentation.dto.request.SmsVerifyRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendApplication {

    @Value("${spring.mail.username}")
    private String emailName; //badbergjr@hanyang.ac.kr

    private final JavaMailSender emailSender;// 실제 메일 전송기 (spring-boot-starter-mail이 제공)
    private final VerificationRepository verificationRepository; // 인증 이력 저장/조회용 JPA 리포지토리

    @Transactional
    public void sendCodeToEmail(
            EmailSendRequestDto request,
            String actor)
    {
        // 인증 코드 생성 및 저장 (기존 동일)
        String authCode = createVerificationCode(
                request.getPurpose(),
                request.getTarget(),
                actor);

        // 이메일 발송 (MimeMessage 사용)
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            // 수신자 이름 설정 (U-sto 혹은 대학물품관리시스템)
            helper.setFrom(new InternetAddress(emailName, "U-sto", "UTF-8"));
            helper.setTo(request.getTarget());
            helper.setSubject("[U-sto] 이메일 인증 번호 : " + authCode);

            // 본문 내용 (HTML 태그 사용 for 크기 조절)
            String body =
                    """
                            <h1>대학물품관리시스템의 표준 U-sto</h1>
                            <h3>요청하신 인증번호입니다.</h3>
                            <h1>%s</h1>
                            <p>5분 안에 입력해주세요.</p>
                            """.formatted(authCode);

            helper.setText(body, true); // HTML을 쓰겠다는 뜻
            emailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("메일 발송 실패 target={}, purpose={}, err=",
                    request.getTarget(),
                    request.getPurpose(), e);
            throw new IllegalStateException("이메일 인증번호 발송 실패");
        }
    }

    /**
     * 인증 코드 생성 + DB 저장/갱신
     *
     * @param purpose
     * @param target
     * @return
     */
    private String createVerificationCode(
            VerificationPurpose purpose,
            String target,
            String actor
    ) {
        String generateRandomCode = generateRandomCode(); //인증번호 생성
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5); //5분 제한

        Verification verification = verificationRepository
                .find(target, VerificationType.EMAIL, purpose)
                .orElse(Verification.builder()
                        .creBy(actor)
                        .purpose(purpose)
                        .target(target)
                        .type(VerificationType.EMAIL)
                        .code(generateRandomCode)
                        .expiresAt(expiresAt)
                        .isVerified(false)
                        .build() // ㄱㄱ씽
                );

        if (verification.getId() != null) { //이미 있다고?
            verification.renew(generateRandomCode, expiresAt); // 그러면 재전송이네? -> 코드 및 만료시간 갱신, 인증 상태 초기화
        }

        verificationRepository.save(verification); //DB 저장
        return generateRandomCode;
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}
