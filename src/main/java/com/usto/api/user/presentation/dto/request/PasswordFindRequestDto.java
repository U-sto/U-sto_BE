package com.usto.api.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordFindRequestDto {

    @NotBlank
    String usrId;
}
