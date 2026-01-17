package com.usto.api.user.presentation.controller;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.SignupApplication;
import com.usto.api.user.application.UserDeleteApplication;
import com.usto.api.user.application.UserUpdateApplication;
import com.usto.api.user.domain.UserPrincipal;
import com.usto.api.user.presentation.dto.request.SignupRequestDto;
import com.usto.api.user.presentation.dto.request.UserDeleteRequestDto;
import com.usto.api.user.presentation.dto.request.UserUpdateRequestDto;
import com.usto.api.user.presentation.dto.response.UserUpdateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "user-controller", description = "회원 정보 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final SignupApplication signupApplication;
    private final UserUpdateApplication userUpdateApplication;
    private final UserDeleteApplication userDeleteApplication;

    @PostMapping("sign-up")
    @Operation(summary = "회원 가입")
    public ApiResponse<?> signup(
            @RequestBody SignupRequestDto request,
            HttpSession session
    ){

        String verifiedEmail = (String) session.getAttribute("signup.auth.email");

        String verifiedSms = (String) session.getAttribute("signup.auth.sms");

        Boolean isExistsUsrID = (Boolean) session.getAttribute("exists.auth.usrId.exists");
        String checkedUsrId = (String) session.getAttribute(("exists.auth.usrId.target"));
        if(isExistsUsrID == null || !checkedUsrId.equals(request.getUsrId())){
            throw new BusinessException("아이디 중복확인이 필요합니다.");
        }

        signupApplication.signup(
                request.getUsrId(),
                request.getUsrNm(),
                request.getPwd(),
                request.getOrgCd(),
                verifiedEmail,
                verifiedSms);

        //세션 삭제
        session.removeAttribute("exists.auth.usrId");
        session.removeAttribute("signup.preauth.email");
        session.removeAttribute("signup.preauth.sms");
        session.removeAttribute("signup.preauth.emailVerifiedAt");
        session.removeAttribute("signup.preauth.expiresAt");

        return ApiResponse.ok("회원가입 완료");
    }

    @PutMapping("/update")
        @Operation(summary = "회원 수정")
        public ApiResponse<UserUpdateResponseDto> updateUser(
                @Valid @RequestBody UserUpdateRequestDto request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
            ) {

        UserUpdateResponseDto result = userUpdateApplication.update(userPrincipal.getUsername(),request);

        return ApiResponse.ok(
                    "회원정보가 정상적으로 수정되었습니다.",
                    result
            );
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴")
    public ApiResponse<?> deleteMe(
            @AuthenticationPrincipal UserPrincipal me,
            @RequestBody UserDeleteRequestDto request
    ) {
        userDeleteApplication.deleteMe(me, request);
        return ApiResponse.ok("회원 탈퇴 완료");
    }
}
