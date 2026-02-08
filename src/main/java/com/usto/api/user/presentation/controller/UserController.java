package com.usto.api.user.presentation.controller;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.*;
import com.usto.api.user.domain.model.UserPrincipal;
import com.usto.api.user.presentation.dto.request.*;
import com.usto.api.user.presentation.dto.response.UserInfoResponse;
import com.usto.api.user.presentation.dto.response.UserPwdUpdateResponse;
import com.usto.api.user.presentation.dto.response.UserSmsUpdateResponse;
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
            @RequestBody SignupRequest request,
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
        session.removeAttribute("exists.auth.usrId.exists");
        session.removeAttribute("exists.auth.usrId.target");
        session.removeAttribute("signup.auth.email");
        session.removeAttribute("signup.auth.sms");
        session.removeAttribute("signup.auth.emailVerifiedAt");
        session.removeAttribute("signup.auth.expiresAt");

        return ApiResponse.ok("회원가입 완료");
    }

    @GetMapping("/info")
        @Operation(summary = "회원 정보")
        public ApiResponse<UserInfoResponse> infoUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal
            ) {

        UserInfoResponse result = userUpdateApplication.info(userPrincipal.getUsername());

        return ApiResponse.ok(
                    "회원정보 조회 성공",
                    result
            );
    }

    @PatchMapping("/update/password")
    @Operation(summary = "회원수정 - 비밀번호 변경")
    public ApiResponse<UserPwdUpdateResponse> updatePwd(
            @Valid @RequestBody PasswordResetRequestForInfo request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {

        UserPwdUpdateResponse result = userUpdateApplication.updatePwd(
                userPrincipal.getUsername(),
                request.getOldPwd(),
                request.getNewPwd());

        return ApiResponse.ok(
                "비밀번호가 정상적으로 수정되었습니다.",
                result
        );
    }

    @PatchMapping("/update/sms")
    @Operation(summary = "휴대폰 번호 변경")
    public ApiResponse<UserSmsUpdateResponse> updateSms(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpSession session
    ) {

        String verifiedSms = (String) session.getAttribute("resetPwd.auth.sms");

        UserSmsUpdateResponse result = userUpdateApplication.updateSms(userPrincipal.getUsername(),verifiedSms);

        session.removeAttribute("resetPwd.auth.sms");
        return ApiResponse.ok(
                "휴대폰 번호가 정상적으로 수정되었습니다.",
                result
        );
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴")
    public ApiResponse<?> deleteMe(
            @AuthenticationPrincipal UserPrincipal me,
            @RequestBody UserDeleteRequest request
    ) {
        userDeleteApplication.deleteMe(me, request);
        return ApiResponse.ok("회원 탈퇴 완료");
    }
}
