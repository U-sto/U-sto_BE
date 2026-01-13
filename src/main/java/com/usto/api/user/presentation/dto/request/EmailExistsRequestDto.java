package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailExistsRequestDto {

    @NotBlank(message = "이메일을 입력해주세요")
    @Schema(example = "example@usto.com")
    @Email
    private String email;


}
