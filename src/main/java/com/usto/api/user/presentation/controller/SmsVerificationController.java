/*
 * SmsConroller (레거시 문자 인증 전용 컨트롤러)
 * - 역할: 문자(SMS) 인증만을 위한 구 엔드포인트(/send-sms, /check-sms)를 제공합니다.
 * - 비고: 통합 API(VerificationController의 /send, /check) 사용을 권장합니다.
 */
package com.usto.api.user.presentation.controller;


import com.usto.api.user.application.SmsSendApplication;
import com.usto.api.user.application.SmsVerificationApplication;
import com.usto.api.user.presentation.dto.request.SmsSendRequestDto;
import com.usto.api.user.presentation.dto.request.SmsVerifyRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/verification/sms")
@RequiredArgsConstructor
public class SmsVerificationController {

    private final SmsVerificationApplication smsVerifyApplication;
    private final SmsSendApplication smsSendApplication;

    @PostMapping("/send")
    public ResponseEntity<String> sendSms(
            @Valid
            @RequestBody SmsSendRequestDto request,
            HttpServletRequest http

    ) {
        String actor = resolveActor(http);   // 아래 메서드

        smsSendApplication.sendCodeToSms(request,actor);

        return ResponseEntity.ok("인증번호가 문자로 발송되었습니다.");
    }

    @PostMapping("/check")
    public ResponseEntity<String> verifyCode(
            @Valid
            @RequestBody
            SmsVerifyRequestDto request
    )
    {
        smsVerifyApplication.verifyCode(request);

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
