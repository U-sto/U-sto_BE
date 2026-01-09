package com.usto.api.user.presentation.dto.request;

import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SmsSendRequestDto {

    @NotNull
    private VerificationPurpose purpose;

    @NotBlank
    @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
    private String target; //01012345678

    private VerificationType type = VerificationType.SMS;
}
