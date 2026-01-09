package com.usto.api.user.presentation.dto.response;

import com.usto.api.user.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateResponseDto {

    private String usrId; //표시

    private String usrNm;

    @Email
    private String email;

    @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
    private String sms;

    private String orgNm; //표시

    private Role role;// 역할
}
