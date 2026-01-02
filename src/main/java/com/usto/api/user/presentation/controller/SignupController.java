package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.SignupApplication;
import com.usto.api.user.presentation.dto.request.SignupRequestDto;
import com.usto.api.user.presentation.dto.response.SmsExistsResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SignupController {

    private final SignupApplication signupApplication;

    @PostMapping("/api/users")
    public ApiResponse<?> signup(
            @RequestBody SignupRequestDto request,
            HttpSession session
    ){
        String verifiedEmail = (String)session.getAttribute("signup.preauth.email");
        if (verifiedEmail == null) {
            return ApiResponse.fail("이메일 인증이 필요합니다");
        }

        String verifiedSms = (String) session.getAttribute("signup.preauth.sms");
        if (verifiedSms == null) {
            return ApiResponse.fail("휴대폰 인증이 필요합니다");
        }

        log.info("[SIGNUP] orgCd={}", request.getOrgCd());


        signupApplication.signup(request,verifiedEmail,verifiedSms);

        //세션 삭제
        session.removeAttribute("signup.preauth.email");
        session.removeAttribute("signup.preauth.sms");
        session.removeAttribute("signup.preauth.emailVerifiedAt");
        session.removeAttribute("signup.preauth.expiresAt");

        return ApiResponse.ok("회원가입 완료");
    }
}
