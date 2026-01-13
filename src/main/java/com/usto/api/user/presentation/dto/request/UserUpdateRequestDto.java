package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {

    @Schema(example = "사용자 이름")
    private String newUsrNm;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Schema(example = "user@example.com")
    private String newEmail;

    @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
    @Schema(example = "01000000000")
    private String newSms;

    @Schema(example = "newPw1234!")
    private String newPw;
}
