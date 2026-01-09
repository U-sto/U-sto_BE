package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.EmailExistsApplication;
import com.usto.api.user.application.SmsExistsApplication;
import com.usto.api.user.application.UserIdExistsApplication;
import com.usto.api.user.presentation.dto.response.EmailExistsResponseDto;
import com.usto.api.user.presentation.dto.response.SmsExistsResponseDto;
import com.usto.api.user.presentation.dto.response.UsrIdExistsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "exists-controller", description = "중복 확인 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/exists")
public class ExistsController {

    private final EmailExistsApplication emailExistsApplication;
    private final SmsExistsApplication smsExistsApplication;
    private final UserIdExistsApplication userIdExistsApplication;


    @GetMapping("/email")
    @Operation(summary = "이메일 중복 확인")
    public ApiResponse<?> existsByEmail(
            @RequestParam(required = false)
            String email
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

    @GetMapping("/sms")
    @Operation(summary = "전화번호 중복 확인")
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

    @GetMapping("/user-id   ")
    @Operation(summary = "아이디 중복 확인")
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