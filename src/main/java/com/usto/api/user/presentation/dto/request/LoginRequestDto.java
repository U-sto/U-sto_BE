package com.usto.api.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDto {

    @NotBlank
    String usrId;

    @NotBlank
    String pwd;
}
