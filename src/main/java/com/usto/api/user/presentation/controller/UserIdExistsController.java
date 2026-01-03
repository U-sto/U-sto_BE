package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.UserIdExistsApplication;
import com.usto.api.user.presentation.dto.response.UsrIdExistsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/user-id")
public class UserIdExistsController {

    private final UserIdExistsApplication userIdExistsApplication;

    @GetMapping("/exists")
    public ApiResponse<?> existsByUsrId(
            @RequestParam(required = false)
            String usrId
    ) {
        if (usrId == null || usrId.trim().isEmpty()) {
            return ApiResponse.fail("아이디을 써주세요");
        }

        boolean exists = userIdExistsApplication.existsByUsrId(usrId.trim());

        if (exists) {
            return ApiResponse.fail("이미 가입된 아이디입니다", new UsrIdExistsResponseDto(true));
        }

        return ApiResponse.ok("이용 가능한 아이디입니다", new UsrIdExistsResponseDto(false));
    }
}
