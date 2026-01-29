package com.usto.api.user.presentation.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSmsUpdateResponse {

    private String usrId;

    @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
    @NotBlank(message = "새 전회번호를 입력해주세요")
    private String newSms;
}
