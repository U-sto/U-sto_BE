package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordResetRequest {

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Schema(example = "123qwe!")
    private String pwd;

    @NotBlank(message = "비밀번호 확인란을 입력해주세요")
    @Schema(example = "123qwe!")
    private String pwdConfirm;
}
