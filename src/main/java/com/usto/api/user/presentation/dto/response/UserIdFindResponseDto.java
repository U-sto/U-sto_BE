package com.usto.api.user.presentation.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserIdFindResponseDto {

    @NotBlank
    String usrId;
}
