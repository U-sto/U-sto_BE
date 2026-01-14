package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.*;
import com.usto.api.user.presentation.dto.request.PasswordResetRequestDto;
import com.usto.api.user.presentation.dto.response.UserIdFindResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "find-controller", description = "찾기 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/find")
public class FindController {

    private final UserIdFindApplication userIdFindApplication;
    private final PasswordUpdateApplication passwordUpdateApplication;

    @PostMapping("/user-id")
    @Operation(summary = "아이디 찾기")
    public ApiResponse<?> findUserId(
            HttpSession session
    ) {

        String email = (String) session.getAttribute("findId.auth.email");
        if (email == null) {
            return ApiResponse.fail("이메일 인증이 필요합니다");
        }

        String usrNm = (String) session.getAttribute("findId.auth.usrNm");
        String userId = userIdFindApplication.findUserIdByEmail(email);

        session.removeAttribute("findId.auth.usrNm");
        session.removeAttribute("findId.auth.email");
        session.removeAttribute("findId.auth.expiresAt");

        return ApiResponse.ok("아이디 찾기 완료", new UserIdFindResponseDto(userId));
    }


    @PostMapping("/password")
    @Operation(summary = "비밀번호 재설정")
    public ApiResponse<?> FindPassword(
            @RequestBody PasswordResetRequestDto request,
            HttpSession session
    )
    {
        String email = (String) session.getAttribute("findPassword.auth.email");
        String usrId = (String) session.getAttribute("findPassword.auth.usrId");

        if (email == null || usrId == null) {  // ← 둘 다 먼저 체크
            return ApiResponse.fail("이메일 인증이 필요합니다.");
        }

        if(!request.getPwd().equals(request.getPwdConfirm())){
            return ApiResponse.fail("두 비밀번호가 일치하지 않습니다.");
        }

        passwordUpdateApplication.updatePassword(usrId, request.getPwd());

        session.removeAttribute("findPassword.auth.email");
        session.removeAttribute("findPassword.auth.expiresAt");
        session.removeAttribute("findPassword.auth.usrId");

        return ApiResponse.ok("비밀번호 재설정 완료");
    }
}