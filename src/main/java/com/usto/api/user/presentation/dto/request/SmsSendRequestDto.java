package com.usto.api.user.presentation.dto.request;

import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SmsSendRequestDto {

    @NotBlank (message = "전화번호를 입력해주세요")
    @Schema(example = "01012345678")
    @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
    private String target;

    @NotNull(message = "인증 목적을 명시해주세요.")
    @Schema(example = "SIGNUP/RESET_PASSWORD")
    private VerificationPurpose purpose;

}
