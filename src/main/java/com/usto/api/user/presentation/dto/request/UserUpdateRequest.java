package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

    //회원 수정에 인증 과정 별도로 구현 필요함 !

    @Schema(example = "홍길동")
    private String newUsrNm;

    @Pattern(regexp = "^[0-9]{11}$", message = "전화번호는 숫자 11자리여야 합니다.")
    @Schema(example = "01012345678")
    private String newSms;

    @Schema(example = "newPw1234!")
    private String newPw;
}
