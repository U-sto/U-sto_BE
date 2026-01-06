package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.LoginApplication;
import com.usto.api.user.domain.model.LoginUser;
import com.usto.api.user.presentation.dto.request.LoginRequestDto;
import com.usto.api.user.presentation.dto.response.LoginResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LogController {

    private final LoginApplication loginApplication;
    //private final LogoutApplication logoutApplication;

    @PostMapping("/login")
    public ApiResponse<?> login(
            @Valid @RequestBody LoginRequestDto request
    ) {
        LoginUser user = loginApplication.login(
                request.getUsrId(),
                request.getPwd()
        );

        return ApiResponse.ok(
                "로그인 성공",
                new LoginResponseDto(
                        user.getUsrId(),
                        user.getUsrNm()
                )
        );
    }
}
