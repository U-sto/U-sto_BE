package com.usto.api.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.usto.api.user.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {

    @Schema(nullable = true , example = "")
    private String newUsrNm;

    @Email
    @Schema(nullable = true, example = "")
    private String newEmail;

    @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
    @Schema(nullable = true, example = "01000000000")
    private String newSms;

    @Schema(nullable = true, example = "")
    private String newPw;
}
