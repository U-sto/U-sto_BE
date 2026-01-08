package com.usto.api.user.presentation.controller;

import com.usto.api.user.application.EmailSendApplication;
import com.usto.api.user.application.EmailVerificationApplication;
import com.usto.api.user.application.SmsSendApplication;
import com.usto.api.user.application.SmsVerificationApplication;
import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.presentation.dto.request.EmailSendRequestDto;
import com.usto.api.user.presentation.dto.request.EmailVerifyRequestDto;
import com.usto.api.user.presentation.dto.request.SmsSendRequestDto;
import com.usto.api.user.presentation.dto.request.SmsVerifyRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final EmailVerificationApplication emailVerifyApplication;
    private final EmailSendApplication emailSendApplication;

    private final SmsVerificationApplication smsVerifyApplication;
    private final SmsSendApplication smsSendApplication;

    @PostMapping("/email/send")
    public ResponseEntity<String> sendEmail(
            @Valid
            @RequestBody EmailSendRequestDto request,
            HttpServletRequest http
    )
    {
        String actor = resolveActor(http);   // 아래 메서드
        emailSendApplication.sendCodeToEmail(request,actor);

        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }


    @PostMapping("/email/check")
    public ResponseEntity<String> verifyEmail(
            @Valid
            @RequestBody EmailVerifyRequestDto request,
            HttpSession session

    ) {
        emailVerifyApplication.verifyCode(request);

        if (request.getPurpose().equals(VerificationPurpose.SIGNUP)) {
            session.setAttribute("signup.preauth.email", request.getTarget());
            session.setAttribute("signup.preauth.expiresAt", LocalDateTime.now().plusMinutes(15));
        }else if(request.getPurpose().equals(VerificationPurpose.FIND_ID)) {
            session.setAttribute("find.preauth.email", request.getTarget());
            session.setAttribute("find.preauth.expiresAt", LocalDateTime.now().plusMinutes(15));
        }else if(request.getPurpose().equals(VerificationPurpose.RESET_PASSWORD)){
            session.setAttribute("find.preauth.email", request.getTarget());
            session.setAttribute("find.preauth.expiresAt", LocalDateTime.now().plusMinutes(15));
        }


        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");

    }

    @PostMapping("/sms/send")
    public ResponseEntity<String> sendSms(
            @Valid
            @RequestBody SmsSendRequestDto request,
            HttpServletRequest http

    ) {
        String actor = resolveActor(http);   // 아래 메서드

        smsSendApplication.sendCodeToSms(request,actor);

        return ResponseEntity.ok("인증번호가 문자로 발송되었습니다.");
    }

    @PostMapping("/sms/check")
    public ResponseEntity<String> verifyCode(
            @Valid
            @RequestBody
            SmsVerifyRequestDto request,
            HttpSession session
    )
    {
        smsVerifyApplication.verifyCode(request);

        if (request.getPurpose().equals(VerificationPurpose.SIGNUP)) {
            session.setAttribute("signup.preauth.sms", request.getTarget());
            session.setAttribute("signup.preauth.expiresAt", LocalDateTime.now().plusMinutes(15));
        }else if(request.getPurpose().equals(VerificationPurpose.FIND_ID)) {
            session.setAttribute("find-userid.preauth.sms", request.getTarget());
            session.setAttribute("find-userid.preauth.expiresAt", LocalDateTime.now().plusMinutes(15));
        }else if(request.getPurpose().equals(VerificationPurpose.RESET_PASSWORD)){
            session.setAttribute("reset-password.preauth.sms", request.getTarget());
            session.setAttribute("reset-password.preauth.expiresAt", LocalDateTime.now().plusMinutes(15));
        }

        return ResponseEntity.ok("전화번호 인증이 완료되었습니다.");
    }





    private String resolveActor(HttpServletRequest http) {
        // 로그인 기반이면 여기서 SecurityContext에서 usrId 꺼냄
        // 아니면 IP+UA
        String ip = http.getRemoteAddr();
        String ua = http.getHeader("User-Agent");
        return "ANON ip=" + ip + " ua=" + (ua == null ? "-" : ua);
    }
}
