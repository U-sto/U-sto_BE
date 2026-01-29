package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserDeleteRequest {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    @Schema(example = "123qwe!")
    private String currentPw;
}
