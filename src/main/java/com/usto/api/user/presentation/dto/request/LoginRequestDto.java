package com.usto.api.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDto {

    @NotBlank(message = "아이디를 입력해주세요")
    String usrId;

    @NotBlank(message = "비밀번호를 입력해주세요")
    String pwd;
}
