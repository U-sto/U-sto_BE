package com.usto.api.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserIdFindRequestDto {

    @NotBlank(message = "이름을 입력해주세요.")
    @Schema(example = "ustoId")
    private String usrNm;

    private static final String FIXED_DOMAIN = "hanyang.ac.kr";

    @NotBlank(message = "이메일을 입력해주세요")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]{1,64}$")
    private String emailId;

    @JsonIgnore // 응답에 노출 방지 (필요 시)
    public String getEmail() {
        return emailId.trim() + "@" + FIXED_DOMAIN;
    }

}
