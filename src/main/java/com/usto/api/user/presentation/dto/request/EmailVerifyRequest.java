package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class EmailVerifyRequest {

    @NotBlank(message = "인증번호를 입력해주세요")
    @Schema(example = "123456")
    @Pattern(regexp = "^[0-9]{6}$", message = "인증코드는 6자리 숫자여야 합니다.")
    private String code;
}
