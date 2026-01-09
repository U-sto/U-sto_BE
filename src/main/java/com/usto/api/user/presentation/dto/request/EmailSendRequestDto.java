package com.usto.api.user.presentation.dto.request;

import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class   EmailSendRequestDto {

    @NotNull
    private VerificationPurpose purpose;

    @NotBlank
    @Email
    private String target;//example@naver.com

    private VerificationType type = VerificationType.EMAIL;
}
