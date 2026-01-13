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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "verification-controller", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final EmailVerificationApplication emailVerifyApplication;
    private final EmailSendApplication emailSendApplication;

    private final SmsVerificationApplication smsVerifyApplication;
    private final SmsSendApplication smsSendApplication;

    //회원가입
    @PostMapping("/email/send")
    @Operation(summary = "이메일 인증번호 전송")
    public ResponseEntity<String> sendEmail(
            @Valid @RequestBody EmailSendRequestDto request,
            @RequestParam VerificationPurpose purpose,
            HttpServletRequest http
    )
    {
        String actor = resolveActor(http);
        emailSendApplication.sendCodeToEmail(
                request,
                purpose,
                actor);

        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }


    @PostMapping("/email/check")
    @Operation(summary = "이메일 인증번호 확인")
    public ResponseEntity<String> verifyEmail(
            @Valid @RequestBody EmailVerifyRequestDto request,
            @RequestParam VerificationPurpose purpose,
            HttpSession session

    ) {
        emailVerifyApplication.verifyCode(request,purpose);

        String sessionPrefix = getSessionPrefix(purpose);
        session.setAttribute( sessionPrefix +"preauth.email", request.getTarget());
        session.setAttribute(sessionPrefix +"preauth.expiresAt", LocalDateTime.now().plusMinutes(15));

        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");

    }

    @PostMapping("/sms/send")
    @Operation(summary = "휴대폰 인증번호 전송")
    public ResponseEntity<String> sendSms(
            @Valid @RequestBody SmsSendRequestDto request,
            @RequestParam VerificationPurpose purpose,
            HttpServletRequest http

    ) {
        String actor = resolveActor(http);

        smsSendApplication.sendCodeToSms(
                request,
                purpose,
                actor);

        return ResponseEntity.ok("인증번호가 문자로 발송되었습니다.");
    }

    @PostMapping("/sms/check")
    @Operation(summary = "휴대폰 인증번호 확인")
    public ResponseEntity<String> verifyCode(
            @Valid @RequestBody
            SmsVerifyRequestDto request,
            @RequestParam VerificationPurpose purpose,
            HttpSession session
    )
    {
        smsVerifyApplication.verifyCode(request,purpose);


        String sessionPrefix = getSessionPrefix(purpose);
        session.setAttribute( sessionPrefix +"preauth.email", request.getTarget());
        session.setAttribute(sessionPrefix +"preauth.expiresAt", LocalDateTime.now().plusMinutes(15));


        return ResponseEntity.ok("전화번호 인증이 완료되었습니다.");
    }

    //메서드들
    private String resolveActor(HttpServletRequest http) {
        // 로그인 기반이면 여기서 SecurityContext에서 usrId 꺼냄
        // 아니면 IP+UA
        String ip = http.getRemoteAddr();
        String ua = http.getHeader("User-Agent");
        return "ANON ip=" + ip + " ua=" + (ua == null ? "-" : ua);
    }

    private String getSessionPrefix(VerificationPurpose purpose) {
        return switch (purpose) {
            case SIGNUP -> "signup";
            case FIND_ID -> "findId";
            case RESET_PASSWORD -> "findPassword";
        };
    }
}
