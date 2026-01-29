package com.usto.api.user.presentation.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPwdUpdateResponseDto {

    private String usrId;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    private String newPwd;
}
