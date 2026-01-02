/*
 * EmailController
 * - 역할: 구(舊) 방식의 이메일 인증 전용 엔드포인트를 제공합니다.
 * - 비고: 현재는 VerificationController의 통합 API(/send, /check)를 권장하지만,
 *        레거시/호환성 유지를 위해 /send-email, /check-email을 그대로 둡니다.
 */
package com.usto.api.user.presentation.controller;

import com.usto.api.user.application.EmailSendApplication;
import com.usto.api.user.application.EmailVerificationApplication;
import com.usto.api.user.presentation.dto.request.EmailSendRequestDto;
import com.usto.api.user.presentation.dto.request.EmailVerifyRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/verif/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationApplication emailVerifyApplication;
    private final EmailSendApplication emailSendApplication;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(
            @Valid
            @RequestBody EmailSendRequestDto request,
            HttpServletRequest http
    ) {
        String actor = resolveActor(http);   // 아래 메서드
        emailSendApplication.sendCodeToEmail(request,actor);

        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }


    @PostMapping("/check")
    public ResponseEntity<String> verifyEmail(
            @Valid
            @RequestBody EmailVerifyRequestDto request
    ) {
        emailVerifyApplication.verifyCode(request);

        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    private String resolveActor(HttpServletRequest http) {
        // 로그인 기반이면 여기서 SecurityContext에서 usrId 꺼냄
        // 아니면 IP+UA
        // 일반적으로는 UsrID로 해야함.
        String ip = http.getRemoteAddr();
        String ua = http.getHeader("User-Agent");
        return "ANON ip=" + ip + " ua=" + (ua == null ? "-" : ua);
    }
}
