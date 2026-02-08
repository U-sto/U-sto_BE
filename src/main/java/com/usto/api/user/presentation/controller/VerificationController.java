package com.usto.api.user.presentation.controller;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.utils.ApiResponse;
import com.usto.api.common.utils.SessionKeys;
import com.usto.api.user.application.*;
import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.presentation.dto.request.EmailSendRequest;
import com.usto.api.user.presentation.dto.request.EmailVerifyRequest;
import com.usto.api.user.presentation.dto.request.SmsSendRequest;
import com.usto.api.user.presentation.dto.request.SmsVerifyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "verification-controller", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth/verification")
@RequiredArgsConstructor
@Slf4j
public class VerificationController {

    private final EmailVerificationApplication emailVerifyApplication;
    private final EmailSendApplication emailSendApplication;
    private final SmsVerificationApplication smsVerifyApplication;
    private final SmsSendApplication smsSendApplication;

    @PostMapping("/email/send")
    @Operation(summary = "이메일 인증번호 전송")
    public ApiResponse<?> sendEmail(
            @Valid @RequestBody EmailSendRequest request,
            HttpServletRequest http,
            HttpSession session

    )
    {
        VerificationPurpose purpose = VerificationPurpose.determinePurpose(
                request.getUsrId(),
                request.getUsrNm()
        );

        if(purpose.equals(VerificationPurpose.SIGNUP)){
            Boolean isExistsEmail = (Boolean) session.getAttribute(SessionKeys.EXISTS_EMAIL_CHECKED);
            String checkedEmail = (String) session.getAttribute(SessionKeys.EXISTS_EMAIL_TARGET);
            if(isExistsEmail == null || !checkedEmail.equals(request.getEmail())){
                throw new BusinessException("이메일 중복확인이 필요합니다.");
            }
        }

        if(purpose.equals(VerificationPurpose.FIND_ID)){
            session.setAttribute(SessionKeys.EMAIL_PENDING_USER_NAME, request.getUsrNm());

        }
        if(purpose.equals(VerificationPurpose.RESET_PASSWORD)){
            session.setAttribute(SessionKeys.EMAIL_PENDING_USER_ID, request.getUsrId());

        }

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        session.setAttribute(SessionKeys.EMAIL_PENDING_PURPOSE, purpose);
        session.setAttribute(SessionKeys.EMAIL_PENDING_TARGET, request.getEmail());
        session.setAttribute(SessionKeys.EMAIL_PENDING_SENT_AT, expiresAt);

        String actor = resolveActor(http);
        emailSendApplication.sendCodeToEmail(
                request,
                purpose,
                actor);

        session.removeAttribute(SessionKeys.EXISTS_EMAIL_CHECKED);
        session.removeAttribute(SessionKeys.EXISTS_EMAIL_TARGET);

        return ApiResponse.ok("인증번호가 발송되었습니다.");
    }


    @GetMapping("/email/check")
    @Operation(summary = "이메일 인증번호 확인")
    public ApiResponse<?> verifyEmail(
            @Parameter(description = "인증번호 ", example = "123456")
            @RequestParam(name = "code") String code,
            HttpSession session

    ) {

        VerificationPurpose purpose = (VerificationPurpose) session.getAttribute(SessionKeys.EMAIL_PENDING_PURPOSE);

        if (purpose == null) {
            throw new BusinessException("인증번호 발송 내역이 없습니다. no_purpose");
        }

        String target = (String) session.getAttribute("email.pending.target");
        if (target == null) {
            throw new BusinessException("인증번호 발송 내역이 없습니다. no_target");
        }

        emailVerifyApplication.verifyCode(code,target,purpose);

        session.removeAttribute(SessionKeys.EMAIL_PENDING_PURPOSE);
        session.removeAttribute(SessionKeys.EMAIL_PENDING_TARGET);
        session.removeAttribute(SessionKeys.EMAIL_PENDING_SENT_AT);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        switch (purpose) {
            case FIND_ID -> {
                String usrNm = (String) session.getAttribute(SessionKeys.EMAIL_PENDING_USER_NAME);
                session.setAttribute(SessionKeys.FIND_ID_AUTH_USER_NAME, usrNm);
                session.setAttribute(SessionKeys.FIND_ID_AUTH_EMAIL, target);
                session.setAttribute(SessionKeys.FIND_ID_AUTH_EXPIRES_AT, expiresAt);
                session.removeAttribute(SessionKeys.EMAIL_PENDING_USER_NAME);
            }
            case RESET_PASSWORD -> {
                String usrId = (String) session.getAttribute(SessionKeys.EMAIL_PENDING_USER_ID);
                session.setAttribute(SessionKeys.RESET_PWD_AUTH_USER_ID, usrId);
                session.setAttribute(SessionKeys.RESET_PWD_AUTH_EMAIL, target);
                session.setAttribute(SessionKeys.RESET_PWD_AUTH_EXPIRES_AT, expiresAt);
                session.removeAttribute(SessionKeys.EMAIL_PENDING_USER_ID);
            }
            case SIGNUP -> {
                session.setAttribute(SessionKeys.SIGNUP_AUTH_EMAIL, target);
                session.setAttribute(SessionKeys.SIGNUP_AUTH_EXPIRES_AT, expiresAt);
            }
        }
        return ApiResponse.ok("이메일 인증이 완료되었습니다.");
    }

    @PostMapping("/sms/send")
    @Operation(summary = "휴대폰 인증번호 전송")
    public ApiResponse<?> sendSms(
            @Valid @RequestBody SmsSendRequest request,
            HttpServletRequest http,
            HttpSession session
    ) {
        Boolean isChecked = (Boolean) session.getAttribute(SessionKeys.EXISTS_SMS_CHECKED);
        String checkedSms = (String) session.getAttribute(SessionKeys.EXISTS_SMS_TARGET);
        if(isChecked == null || !checkedSms.equals(request.getTarget())){
            throw new BusinessException("전화번호 중복확인이 필요합니다.");
        }


        log.debug("[SIGNUP] 전화번호 중복 확인 완료: {}", request.getTarget());

        session.setAttribute(SessionKeys.SMS_PENDING_PURPOSE, request.getPurpose());
        session.setAttribute(SessionKeys.SMS_PENDING_TARGET, request.getTarget());
        session.setAttribute(SessionKeys.SMS_PENDING_SENT_AT, LocalDateTime.now());

        String actor = resolveActor(http);

        smsSendApplication.sendCodeToSms(
                request,
                actor);

        session.removeAttribute(SessionKeys.EXISTS_SMS_CHECKED);
        session.removeAttribute(SessionKeys.EXISTS_SMS_TARGET);

        return ApiResponse.ok("인증번호가 문자로 발송되었습니다.");
    }

    @GetMapping("/sms/check")
    @Operation(summary = "휴대폰 인증번호 확인")
    public ApiResponse<?> verifyCode(
            @Parameter(description = "인증번호 ", example = "123456")
            @RequestParam(name = "code") String code,
            HttpSession session
    )
    {

        VerificationPurpose purpose = (VerificationPurpose) session.getAttribute(SessionKeys.SMS_PENDING_PURPOSE);
        String target = (String) session.getAttribute(SessionKeys.SMS_PENDING_TARGET);

        smsVerifyApplication.verifyCode(code,target,purpose);

        session.removeAttribute(SessionKeys.SMS_PENDING_PURPOSE);
        session.removeAttribute(SessionKeys.SMS_PENDING_TARGET);
        session.removeAttribute(SessionKeys.SMS_PENDING_SENT_AT);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        switch (purpose) {
            case SIGNUP -> {
                session.setAttribute(SessionKeys.SIGNUP_AUTH_SMS, target);
                session.setAttribute(SessionKeys.SIGNUP_AUTH_EXPIRES_AT, expiresAt);
            }
            case RESET_PASSWORD -> {
                session.setAttribute(SessionKeys.RESET_PWD_AUTH_SMS, target);
                session.setAttribute(SessionKeys.RESET_PWD_AUTH_EXPIRES_AT, expiresAt);
            }
            default -> {
                // FIND_ID는 SMS를 사용하지 않음
            }
        }

        return ApiResponse.ok("전화번호 인증이 완료되었습니다.");
    }


    private String resolveActor(HttpServletRequest http) {
        // 로그인 기반이면 여기서 SecurityContext에서 usrId 꺼냄
        // 아니면 IP+UA
        String ip = http.getRemoteAddr();
        String ua = http.getHeader("User-Agent");
        return "ANON ip=" + ip + " ua=" + (ua == null ? "-" : ua);
    }
}
