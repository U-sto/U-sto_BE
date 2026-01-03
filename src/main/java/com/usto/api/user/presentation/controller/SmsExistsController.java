package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.SmsExistsApplication;
import com.usto.api.user.presentation.dto.response.SmsExistsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/sms")
public class SmsExistsController {

    private final SmsExistsApplication smsExistsApplication;

    @GetMapping("/exists")
    public ApiResponse<?> existsBySms(
            @RequestParam(required = false)
            String sms
    ) {
        if (sms == null || sms.trim().isEmpty()) {
            return ApiResponse.fail("전화번호를 써주세요");
        }

        boolean exists = smsExistsApplication.existsBySms(sms.trim());

        if (exists) {
            return ApiResponse.fail("이미 가입된 전화번호입니다", new SmsExistsResponseDto(true));
        }

        return ApiResponse.ok("이용 가능한 전화번호입니다", new SmsExistsResponseDto(false));
    }
}
