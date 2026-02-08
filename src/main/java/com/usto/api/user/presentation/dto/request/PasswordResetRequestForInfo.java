package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PasswordResetRequestForInfo {

    @NotBlank(message = "기존 비밀번호를 입력해주세요")
    @Schema(example = "*123qwe!")
    // 최소 8자, 대소문자/숫자/특수문자 포함
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    private String oldPwd;

    @NotBlank(message = "새 비밀번호를 입력해주세요")
    @Schema(example = "*123qwe!")
    // 최소 8자, 대소문자/숫자/특수문자 포함
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    private String newPwd;
}
