package com.usto.api.user.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserIdFindRequestDto {

    @NotBlank
    String usrNm;

    @Email
    String email;

}
