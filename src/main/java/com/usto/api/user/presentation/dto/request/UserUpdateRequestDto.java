package com.usto.api.user.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {

    private String newUsrNm;

    @Email
    private String newEmail;

    @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
    private String newSms;

    private String newPw;
}
