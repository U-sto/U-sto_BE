package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.EmailExistsApplication;
import com.usto.api.user.application.SmsExistsApplication;
import com.usto.api.user.application.UserIdExistsApplication;
import com.usto.api.user.presentation.dto.response.EmailExistsResponse;
import com.usto.api.user.presentation.dto.response.SmsExistsResponse;
import com.usto.api.user.presentation.dto.response.UsrIdExistsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    private static final String FIXED_DOMAIN = "hanyang.ac.kr";

    @GetMapping("/email")
    @Operation(summary = "이메일 중복 확인")
    public ApiResponse<?> existsByEmail(
            @Parameter(description = "이메일 ID", example = "test1234")
            @RequestParam(name = "emailId")
            @NotBlank(message = "이메일 ID를 입력해주세요")
            @Pattern(regexp = "^[A-Za-z0-9._%+-]{1,64}$", message = "올바른 ID 형식이 아닙니다.")
            String emailId, // DTO 대신 String으로 직접 받음

            HttpSession session
    ) {
        if (emailId == null || emailId.trim().isEmpty()) {
            return ApiResponse.fail("이메일을 써주세요");
        }

        String fullEmail = emailId.trim() + "@" + FIXED_DOMAIN;

        boolean exists = emailExistsApplication.existsByEmail(fullEmail.trim());

        if (exists) {
            return ApiResponse.fail("이미 가입된 이메일입니다",
                    new EmailExistsResponse(true));
        }

        session.setAttribute("exists.auth.email.exists",exists);
        session.setAttribute("exists.auth.email.target",fullEmail);

        return ApiResponse.ok("이용 가능한 이메일입니다",
                new EmailExistsResponse(false));
    }

    @GetMapping("/sms")
    @Operation(summary = "전화번호 중복 확인")
    public ApiResponse<?> existsBySms(
            @Valid @ModelAttribute
            @Parameter(description = "전화번호", example = "01012345678")
            @RequestParam(name = "sms")
            @NotBlank(message = "전화번호를 입력해주세요")
            @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
            String sms,
            HttpSession session
    ) {
        if (sms == null || sms.trim().isEmpty()) {
            return ApiResponse.fail("전화번호를 써주세요");
        }

        boolean exists = smsExistsApplication.existsBySms(sms.trim());

        if (exists) {
            return ApiResponse.fail("이미 가입된 전화번호입니다",
                    new SmsExistsResponse(true));
        }

        session.setAttribute("exists.auth.sms.exists",exists);
        session.setAttribute("exists.auth.sms.target",sms);

        return ApiResponse.ok("이용 가능한 전화번호입니다",
                new SmsExistsResponse(false));
    }

    @GetMapping("/user-id")
    @Operation(summary = "아이디 중복 확인")
    public ApiResponse<?> existsByUsrId(
            @Valid @ModelAttribute
            @Parameter(description = "사용자 아이디", example = "ustoId")
            @RequestParam(name = "usrId")
            @NotBlank(message = "아이디를 입력해주세요.")
            String usrId,
            HttpSession session
    ) {
        if (usrId == null || usrId.trim().isEmpty()) {
            return ApiResponse.fail("아이디를 써주세요");
        }

        boolean exists = userIdExistsApplication.existsByUsrId(usrId.trim());

        if (exists) {
            return ApiResponse.fail("이미 가입된 아이디입니다",
                    new UsrIdExistsResponse(true));
        }

        session.setAttribute("exists.auth.usrId.exists",exists);
        session.setAttribute("exists.auth.usrId.target",usrId);

        return ApiResponse.ok("이용 가능한 아이디입니다",
                new UsrIdExistsResponse(false));
    }

}