package com.usto.api.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @NotBlank(message = "아이디를 입력해주세요")
    @Schema(example = "ustoId")
    private String usrId;

    @NotBlank(message = "이름을 입력해주세요")
    @Schema(example = "김철수")
    private String usrNm;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Schema(example = "123qwe!")
    private String pwd;

    @NotBlank(message = "조직을 입력해주세요.")
    @Schema(example = "HANYANG_ERICA") //프론트에서는 orgNm으로 나와야한다
    private String orgCd;
}
