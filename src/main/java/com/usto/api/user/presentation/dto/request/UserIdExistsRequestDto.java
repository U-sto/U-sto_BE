package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserIdExistsRequestDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Schema(example = "ustoId")
    private String usrId;
}
