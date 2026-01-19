package com.usto.api.user.presentation.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailExistsRequestDto {

    private static final String FIXED_DOMAIN = "hanyang.ac.kr";

    @NotBlank(message = "이메일을 입력해주세요")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]{1,64}$")
    private String emailId;

    @JsonIgnore // 응답에 노출 방지 (필요 시)
    public String getEmail() {
        if (emailId == null) {
            return null;
        }
        return emailId.trim() + "@" + FIXED_DOMAIN;
    }
}
