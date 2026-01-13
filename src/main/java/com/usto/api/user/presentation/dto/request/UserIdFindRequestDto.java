package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserIdFindRequestDto {

    @NotBlank(message = "이름을 입력해주세요.")
    @Schema(example = "ustoId")
    private String usrNm;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Schema(example = "example@usto.com")
    @Email
    private String email;

}
