package com.usto.api.user.presentation.dto.request;

import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class EmailVerifyRequestDto {

    @NotNull
    private VerificationPurpose purpose;

    @NotBlank
    @Email
    private String target; //example@naver.com

    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$", message = "인증코드는 6자리 숫자여야 합니다.")
    private String code;

    private VerificationType type = VerificationType.EMAIL;
}
