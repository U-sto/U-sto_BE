package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank(message = "아이디를 입력해주세요")
    @Schema(example = "ustoId")
    private String usrId;

    @NotBlank(message = "이름을 입력해주세요")
    @Schema(example = "김철수")
    private String usrNm;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Schema(example = "*123qwe!")
    // 최소 8자, 대소문자/숫자/특수문자 포함
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    private String pwd;

    @NotBlank(message = "조직을 입력해주세요.")
    @Schema(example = "700111") //프론트에서는 orgNm으로 나와야한다
    private String orgCd;
}
