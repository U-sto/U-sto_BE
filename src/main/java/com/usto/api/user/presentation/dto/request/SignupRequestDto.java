package com.usto.api.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @NotBlank
    private String usrId;

    @NotBlank
    private String usrNm;

    @NotBlank
    private String pwd;

    @NotBlank
    private String orgCd;
}
