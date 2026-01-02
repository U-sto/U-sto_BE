package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.EmailExistsApplication;
import com.usto.api.user.presentation.dto.response.EmailExistsResponseDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class EmailExistsController {

    private final EmailExistsApplication emailExistsApplication;

    @GetMapping("/api/users/exists")
    public ApiResponse<?> existsByEmail(
            @RequestParam(value = "email", required = false) String email
    ) {
        if (email == null || email.trim().isEmpty()) {
            return ApiResponse.fail("이메일을 써주세요");
        }

        boolean exists = emailExistsApplication.existsByEmail(email.trim());

        if (exists) {
            return ApiResponse.fail("이미 가입된 이메일입니다", new EmailExistsResponseDto(true));
        }

        return ApiResponse.ok("이용 가능한 이메일입니다", new EmailExistsResponseDto(false));
    }
}
