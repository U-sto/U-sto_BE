package com.usto.api.user.presentation.dto.request;

import lombok.Getter;

@Getter
public class PasswordResetRequestDto {

    String pwd;
    String pwdConfirm;
}
