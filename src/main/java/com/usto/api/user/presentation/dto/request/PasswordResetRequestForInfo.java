package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordResetRequestForInfo {

    @NotBlank(message = "기존 비밀번호를 입력해주세요")
    @Schema(example = "123qwe!")
    private String oldPwd;

    @NotBlank(message = "새 비밀번호를 입력해주세요")
    @Schema(example = "123qwe!")
    private String newPwd;
}
