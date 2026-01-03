package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/recovery")
public class RecoveryController {

    @PostMapping("/user-id")
    public ApiResponse<?> findUserId() {
        return ApiResponse.ok("TODO: user-id recovery");
    }

    @PostMapping("/password/reset")
    public ApiResponse<?> resetPassword() {
        return ApiResponse.ok("TODO: password reset");
    }

    @PostMapping("/password/reset/set")
    public ApiResponse<?> setNewPassword() {
        return ApiResponse.ok("TODO: password reset set");
    }
}