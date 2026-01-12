package com.usto.api.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserDeleteRequestDto {

    @NotBlank
    String crrentPw;
}
