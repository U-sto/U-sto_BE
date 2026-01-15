/*
 * EmailVerificationService
 * - 역할: 이메일 인증번호 발송/검증과 만료 데이터 정리를 담당합니다.
 * - 정책: 6자리 숫자 코드, 유효시간 5분, 검증 성공 시 isVerified=true로 마킹합니다.
 * - 저장: Verification 엔티티를 (target=email, type=EMAIL) 키로 관리합니다.
 */
package com.usto.api.user.application;

import com.usto.api.user.domain.model.User;
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
            VerificationPurpose purpose,
            String actor)
    {
        LocalDateTime timeLimit = LocalDateTime.now().plusMinutes(5);

        String code = createVerificationCode(6);

        //기존 내역 확인
        Verification existingVerification = verificationRepository
                .find(
                        request.getEmail(),
                        VerificationType.EMAIL,
                        purpose                )
                .orElse(null);

        Verification verificationToSave;

        if (existingVerification == null) {
            // 기존 내역없어? -> 생성
            verificationToSave = Verification.builder()
                    .creBy(actor)
                    .purpose(purpose)
                    .target(request.getEmail())
                    .type(VerificationType.EMAIL)
                    .code(code)
                    .expiresAt(timeLimit)
                    .isVerified(false)
                    .build();

            log.info("[EMAIL-SEND] 새 인증 생성 - target: {}, purpose: {}",
                    request.getEmail(), purpose);
        } else {
            //재발송
            Verification renewed = existingVerification.renew(code, timeLimit);

            verificationToSave = renewed.toBuilder()
                    .updBy(actor)
                    .build();

            log.info("[EMAIL-SEND] 인증 재발송 - target: {}, purpose: {}",
                    request.getEmail(), purpose);
        }

        verificationRepository.save(verificationToSave);

        //이메일 발송
        try {
            sendEmail(request.getEmail(), code ,purpose);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("[EMAIL-SEND] 이메일 발송 실패 - target: {}", request.getEmail(), e);
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    @Transactional
    public void sendApprovalRequestEmail(User newUser) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(emailName, "U-sto", "UTF-8"));
            helper.setTo(emailName);
            helper.setSubject("[U-sto] 새로운 회원 승인 요청");

            String body = buildApprovalEmailBody(newUser);
            helper.setText(body, true);

            emailSender.send(message);

            log.info("[APPROVAL-REQUEST] 승인 요청 메일 발송 완료 - to: {}, newUser: {}",
                    emailName, newUser.getUsrId());

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("[APPROVAL-REQUEST] 메일 발송 실패 - to: {}", emailName, e);
            throw new RuntimeException("승인 요청 메일 발송 실패", e);
        }
    }

        //메서드
    private void sendEmail(String to, String code ,VerificationPurpose purpose)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(new InternetAddress(emailName, "U-sto", "UTF-8"));
        helper.setTo(to);
        helper.setSubject(getEmailSubject(purpose, code));

        // HTML 본문
        String body = buildEmailBody(code);
        helper.setText(body, true);

        emailSender.send(message);

        log.info("[EMAIL-SEND] 발송 완료 - to: {}, purpose: {}", to, purpose);
    }

    private String getEmailSubject(VerificationPurpose purpose, String code) {
        return switch (purpose) {
            case SIGNUP -> "[U-sto] 회원가입 인증번호 : " + code;
            case FIND_ID -> "[U-sto] 아이디 찾기 인증번호 : " + code;
            case RESET_PASSWORD -> "[U-sto] 비밀번호 재설정 인증번호 : " + code;
        };
    }

    private String createVerificationCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

    //이메일 본문 HTML
    //이메일 인증 부분
    private String buildEmailBody(String code) {
        return """
            <table width="100%%" cellpadding="0" cellspacing="0"
                   style="background:#0F2A44;padding:40px 0;">
              <tr>
                <td align="center">
                  <table width="560" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:14px;
                                overflow:hidden;
                                box-shadow:0 10px 30px rgba(0,0,0,0.25);
                                font-family:'Apple SD Gothic Neo',Arial,sans-serif;">
                    
                    <!-- Header -->
                    <tr>
                      <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                                 padding:32px 40px;text-align:center;">
                        <h1 style="color:#ffffff;font-size:28px;font-weight:700;
                                   margin: 0;letter-spacing:-0.5px;">
                          U-sto 이메일 인증
                        </h1>
                      </td>
                    </tr>
                    
                    <!-- Body -->
                    <tr>
                      <td style="padding: 48px 40px;">
                        <p style="color:#2d3748;font-size:16px;line-height:1.6;margin: 0 0 24px;">
                          안녕하세요! <br>
                          요청하신 인증번호를 안내해 드립니다. 
                        </p>
                        
                        <!-- Code Box -->
                        <div style="background:#f7fafc;border: 2px dashed #cbd5e0;
                                    border-radius:8px;padding:24px;text-align:center;
                                    margin:32px 0;">
                          <p style="color:#718096;font-size:14px;margin:0 0 12px;">
                            인증번호
                          </p>
                          <p style="color:#667eea;font-size:36px;font-weight:700;
                                    margin:0;letter-spacing: 8px;font-family: monospace;">
                            %s
                          </p>
                        </div>
                        
                        <p style="color:#718096;font-size:14px;line-height:1.6;
                                  margin:24px 0 0;">
                          ⏰ 이 인증번호는 <strong style="color:#e53e3e;">5분간 유효</strong>합니다. <br>
                          🔒 본인이 요청하지 않았다면 이 메일을 무시하세요. 
                        </p>
                      </td>
                    </tr>
                    
                    <!-- Footer -->
                    <tr>
                      <td style="background:#f7fafc;padding:24px 40px;
                                 border-top:1px solid #e2e8f0;text-align:center;">
                        <p style="color:#a0aec0;font-size:12px;margin:0;">
                          © 2026 U-sto. 대학물품관리시스템
                        </p>
                      </td>
                    </tr>
                    
                  </table>
                </td>
              </tr>
            </table>
            """.formatted(code);
    }

    private String buildApprovalEmailBody(User newUser) {
        String baseUrl = "http://localhost:8080/api/approval"; //배포 시 서버 url로 변경해야한다.

        String approveAdminUrl = baseUrl + "?action=approve&role=ADMIN&userId=" + newUser.getUsrId();
        String approveManagerUrl = baseUrl + "?action=approve&role=MANAGER&userId=" + newUser. getUsrId();
        String rejectUrl = baseUrl + "?action=reject&userId=" + newUser.getUsrId();

        return """
            <table width="100%%" cellpadding="0" cellspacing="0"
                   style="background:#0F2A44;padding:40px 0;">
              <tr>
                <td align="center">
                  <table width="560" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:14px;
                                overflow:hidden;
                                box-shadow:0 10px 30px rgba(0,0,0,0.25);
                                font-family:'Apple SD Gothic Neo',Arial,sans-serif;">
        
                    <!-- Header -->
                    <tr>
                      <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                                 padding:32px 40px;text-align:center;">
                        <h1 style="color:#ffffff;font-size:28px;font-weight:700;
                                   margin:0;letter-spacing:-0.5px;">
                          새로운 회원 가입 승인 요청
                        </h1>
                      </td>
                    </tr>
        
                    <!-- Body -->
                    <tr>
                      <td style="padding:48px 40px;">
                        <p style="color:#2d3748;font-size:16px;line-height:1.6;margin:0 0 24px;">
                          안녕하세요, DBA님!<br>
                          새로운 회원이 가입을 요청했습니다.
                        </p>
        
                        <!-- 가입자 정보 -->
                        <div style="background:#f7fafc;border-left:4px solid #667eea;
                                    border-radius:8px;padding:20px;margin:24px 0;">
                          <p style="margin:0 0 8px;color:#2d3748;font-size:14px;">
                            <strong>아이디:</strong> %s
                          </p>
                          <p style="margin:0 0 8px;color:#2d3748;font-size:14px;">
                            <strong>이름:</strong> %s
                          </p>
                          <p style="margin:0 0 8px;color:#2d3748;font-size:14px;">
                            <strong>이메일:</strong> %s
                          </p>
                          <p style="margin:0 0 8px;color:#2d3748;font-size:14px;">
                            <strong>조직:</strong> %s
                          </p>
                          <p style="margin:0;color:#2d3748;font-size:14px;">
                            <strong>가입일시:</strong> %s
                          </p>
                        </div>
        
                        <p style="color:#2d3748;font-size:15px;font-weight:700;margin:24px 0 12px;">
                          역할을 선택하여 승인해주세요:
                        </p>
        
                        <!-- 버튼 영역 -->
                        <div style="text-align:center;margin:32px 0;">
                          <a href="%s"
                             style="display:inline-block;background:#4299e1;color:#ffffff;
                                    padding:14px 28px;text-decoration:none;border-radius:8px;
                                    font-weight:700;margin:0 4px 8px;font-size:15px;">
                            조직 관리자로 승인
                          </a>
                          <a href="%s"
                             style="display:inline-block;background:#48bb78;color:#ffffff;
                                    padding:14px 28px;text-decoration:none;border-radius:8px;
                                    font-weight:700;margin:0 4px 8px;font-size:15px;">
                            물품 운용관으로 승인
                          </a>
                          <br>
                          <a href="%s"
                             style="display:inline-block;background:#f56565;color:#ffffff;
                                    padding:14px 28px;text-decoration:none;border-radius:8px;
                                    font-weight:700;margin:8px 4px 0;font-size:15px;">
                            반려하기
                          </a>
                        </div>
        
                        <p style="color:#718096;font-size:14px;line-height:1.6;margin:24px 0 0;">
                          버튼을 클릭하면 자동으로 승인/반려 처리됩니다.
                        </p>
                      </td>
                    </tr>
        
                    <!-- Footer -->
                    <tr>
                      <td style="background:#f7fafc;padding:24px 40px;
                                 border-top:1px solid #e2e8f0;text-align:center;">
                        <p style="color:#a0aec0;font-size:12px;margin:0;">
                          © 2026 U-sto. 대학물품관리시스템
                        </p>
                      </td>
                    </tr>
        
                  </table>
                </td>
              </tr>
            </table>
            """.formatted(
                newUser.getUsrId(),
                newUser.getUsrNm(),
                newUser.getEmail(),
                newUser.getOrgCd(),
                LocalDateTime.now(),
                approveAdminUrl,
                approveManagerUrl,
                rejectUrl
        );
    }

}
