package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.PasswordFindApplication;
import com.usto.api.user.application.PasswordUpdateApplication;
import com.usto.api.user.application.UserIdFindApplication;
import com.usto.api.user.presentation.dto.request.PasswordFindRequestDto;
import com.usto.api.user.presentation.dto.request.PasswordResetRequestDto;
import com.usto.api.user.presentation.dto.request.UserIdFindRequestDto;
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
    private final PasswordFindApplication passwordFindApplication;
    private final PasswordUpdateApplication passwordUpdateApplication;

    @PostMapping("/user-id")
    @Operation(summary = "아이디 찾기")
    public ApiResponse<?> findUserId(
            @RequestBody
            UserIdFindRequestDto request,
            HttpSession session
    ) {

            String userName = userIdFindApplication.findUserNmByUserId(request.getEmail());
        if (!userName.equals(request.getUsrNm())) {
            return ApiResponse.fail("회원 정보가 일치하지 않습니다.."); //인증번호 전송 불가
        }

        //전송 (purpose를 FIND_ID로 해서 구현하기)

        String verifiedEmail = (String) session.getAttribute("find.preauth.email");

        String userId = userIdFindApplication.findUserIdByEmail(verifiedEmail);

        if (userId == null) {
            return ApiResponse.fail("존재하지 않는 회원입니다.");
        }

        if (verifiedEmail == null) {
            return ApiResponse.fail("이메일 인증이 필요합니다");
        }

        session.removeAttribute("find.preauth.usrId");
        session.removeAttribute("find.preauth.emailVerifiedAt");
        session.removeAttribute("find.preauth.expiresAt");

        return ApiResponse.ok("아이디 찾기 완료", new UserIdFindResponseDto(userId)); // <- 이 부분을 Dto로 풀고 싶은데 어떻게 해야할지 모르겠음

    }


    @PostMapping("/password")
    @Operation(summary = "비밀번호 찾기")
    public ApiResponse<?> findPassword(
            @RequestBody
            PasswordFindRequestDto request,
            HttpSession session
    )
    {
        String userId = userIdFindApplication.findUserIdByEmail(request.getEmail());
        if (!userId.equals(request.getUsrId())) {
            return ApiResponse.fail("회원 정보가 일치하지 않습니다..");
        }

        String verifiedEmail = (String) session.getAttribute("find.preauth.email");

        if (!userId.equals(request.getUsrId())) {
            return ApiResponse.fail("아이디를 확인해주세요.");
        }

        if (verifiedEmail == null) {
            return ApiResponse.fail("이메일 인증이 필요합니다");
        }

        session.removeAttribute("find.preauth.password");
        session.removeAttribute("find.preauth.emailVerifiedAt");
        session.removeAttribute("find.preauth.expiresAt");

        session.setAttribute("find.preauth.usrId",userId); //재설정에서 사용하기 위해서 세션에 담아두기

        return ApiResponse.ok("비밀번호 찾기 완료");
    }

    @PostMapping("/password/update")
    @Operation(summary = "비밀번호 재설정")
    public ApiResponse<?> resetPassword(
            @RequestBody
            PasswordResetRequestDto request,
            HttpSession session
    )
    {
        String userId = (String) session.getAttribute("find.preauth.usrId");

        if (userId == null) {
            return ApiResponse.fail("존재하지 않는 회원입니다.");
        }

        if(!request.getPwd().equals(request.getPwdConfirm())){
            return ApiResponse.fail("두 비밀번호가 일치하지 않습니다.");
        }

        passwordUpdateApplication.updatePwHashByUsrId(userId, request.getPwd());

        return ApiResponse.ok("비밀번호 재설정 완료");
    }

}