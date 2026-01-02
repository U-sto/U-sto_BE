package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.EmailExistsApplication;
import com.usto.api.user.application.UsrIdExistsApplication;
import com.usto.api.user.presentation.dto.response.EmailExistsResponseDto;
import com.usto.api.user.presentation.dto.response.UsrIdExistsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/exists")
public class UsrIdExistsController {

    private final UsrIdExistsApplication usrIdExistsApplication;

    @GetMapping("/usrId")
    public ApiResponse<?> existsByUsrId(
            @RequestParam(required = false)
            String usrId
    ) {
        if (usrId == null || usrId.trim().isEmpty()) {
            return ApiResponse.fail("아이디을 써주세요");
        }

        boolean exists = usrIdExistsApplication.existsByUsrId(usrId.trim());

        if (exists) {
            return ApiResponse.fail("이미 가입된 아이디입니다", new UsrIdExistsResponseDto(true));
        }

        return ApiResponse.ok("이용 가능한 아이디입니다", new UsrIdExistsResponseDto(false));
    }
}
