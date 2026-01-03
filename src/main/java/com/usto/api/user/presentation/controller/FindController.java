package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.presentation.dto.request.UserIdFindRequestDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/find")
public class FindController {

    @PostMapping("/user-id")
    public ApiResponse<?> findUserId(
            @RequestBody
            UserIdFindRequestDto request,
            HttpSession session
    ) {
        String verifiedEmail = (String) session.getAttribute("signup.preauth.email");
        if (verifiedEmail == null) {
            return ApiResponse.fail("이메일 인증이 필요합니다");
        }

        //찾기 기능

        //세션 삭제
        session.removeAttribute("signup.preauth.email");
        session.removeAttribute("signup.preauth.emailVerifiedAt");
        session.removeAttribute("signup.preauth.expiresAt");

        return ApiResponse.ok("아이디 찾기 완료");

    }


    @PostMapping("/password")
    public ApiResponse<?> resetPassword(
            @RequestBody
            UserIdFindRequestDto request,
            HttpSession session
    )
    {
        String verifiedEmail = (String) session.getAttribute("signup.preauth.email");
        if (verifiedEmail == null) {
            return ApiResponse.fail("이메일 인증이 필요합니다");
        }

        //재설정 기능

        //세션 삭제
        session.removeAttribute("signup.preauth.email");
        session.removeAttribute("signup.preauth.emailVerifiedAt");
        session.removeAttribute("signup.preauth.expiresAt");

        return ApiResponse.ok("비밀번호 찾기 완료");
    }

}