package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.EmailExistsApplication;
import com.usto.api.user.application.SmsExistsApplication;
import com.usto.api.user.application.UserIdExistsApplication;
import com.usto.api.user.presentation.dto.request.EmailExistsRequestDto;
import com.usto.api.user.presentation.dto.request.SmsExistRequestDto;
import com.usto.api.user.presentation.dto.request.UserIdExistsRequestDto;
import com.usto.api.user.presentation.dto.response.EmailExistsResponseDto;
import com.usto.api.user.presentation.dto.response.SmsExistsResponseDto;
import com.usto.api.user.presentation.dto.response.UsrIdExistsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "exists-controller", description = "중복 확인 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/exists")
@Validated
public class ExistsController {

    private final EmailExistsApplication emailExistsApplication;
    private final SmsExistsApplication smsExistsApplication;
    private final UserIdExistsApplication userIdExistsApplication;


    @GetMapping("/email")
    @Operation(summary = "이메일 중복 확인")
    public ApiResponse<?> existsByEmail(
            @Valid @RequestBody
            EmailExistsRequestDto request,
            HttpSession session
    ) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ApiResponse.fail("이메일을 써주세요");
        }

        boolean exists = emailExistsApplication.existsByEmail(request.getEmail().trim());

        if (exists) {
            return ApiResponse.fail("이미 가입된 이메일입니다",
                    new EmailExistsResponseDto(true));
        }

        session.setAttribute("exists.auth.email.exists",exists);
        session.setAttribute("exists.auth.email.target",request.getEmail());

        return ApiResponse.ok("이용 가능한 이메일입니다",
                new EmailExistsResponseDto(false));
    }

    @GetMapping("/sms")
    @Operation(summary = "전화번호 중복 확인")
    public ApiResponse<?> existsBySms(
            @Valid @RequestBody
            SmsExistRequestDto request,
            HttpSession session
    ) {
        if (request.getSms() == null || request.getSms().trim().isEmpty()) {
            return ApiResponse.fail("전화번호를 써주세요");
        }

        boolean exists = smsExistsApplication.existsBySms(request.getSms().trim());

        if (exists) {
            return ApiResponse.fail("이미 가입된 전화번호입니다",
                    new SmsExistsResponseDto(true));
        }

        session.setAttribute("exists.auth.sms.exists",exists);
        session.setAttribute("exists.auth.sms.target",request.getSms());

        return ApiResponse.ok("이용 가능한 전화번호입니다",
                new SmsExistsResponseDto(false));
    }

    @GetMapping("/user-id")
    @Operation(summary = "아이디 중복 확인")
    public ApiResponse<?> existsByUsrId(
            @Valid @RequestBody
            UserIdExistsRequestDto request,
            HttpSession session
    ) {
        if (request.getUsrId() == null || request.getUsrId().trim().isEmpty()) {
            return ApiResponse.fail("아이디를 써주세요");
        }

        boolean exists = userIdExistsApplication.existsByUsrId(request.getUsrId().trim());

        if (exists) {
            return ApiResponse.fail("이미 가입된 아이디입니다",
                    new UsrIdExistsResponseDto(true));
        }

        session.setAttribute("exists.auth.usrId.exists",exists);
        session.setAttribute("exists.auth.usrId.target",request.getUsrId());

        return ApiResponse.ok("이용 가능한 아이디입니다",
                new UsrIdExistsResponseDto(false));
    }

}