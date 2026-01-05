package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.LoginApplication;
import com.usto.api.user.application.SignupApplication;
import com.usto.api.user.domain.model.LoginUser;
import com.usto.api.user.presentation.dto.request.LoginRequestDto;
import com.usto.api.user.presentation.dto.response.LoginResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LogControllrt {

    private final LoginApplication loginApplication;
    //private final LogoutApplicaiton logoutApplication;

    @PostMapping("/login")
    public ApiResponse<?> login(
            @RequestBody
            LoginRequestDto request
    )
    {
        if(request.getUsrId().isEmpty()) {
            return ApiResponse.fail("아이디를 입력해주세요");
        }else if(request.getPwd().isEmpty()){
            return ApiResponse.fail("비밀번호를 입력해주세요.");
        }

        LoginUser user =
                loginApplication.login(
                        request.getUsrId(),
                        request.getPwd());

        return ApiResponse.ok(
                "로그인 성공",
                new LoginResponseDto(
                        user.getUsrId(),
                        user.getUsrNm()
                )
        );
    }
}
