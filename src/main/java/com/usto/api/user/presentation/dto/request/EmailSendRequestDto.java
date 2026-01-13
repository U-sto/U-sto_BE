package com.usto.api.user.presentation.dto.request;

import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class EmailSendRequestDto {

    @NotBlank(message = "이메일을 입력해주세요")
    @Schema(example = "example@usto.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String target;
}
