package com.usto.api.user.presentation.dto.request;

import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class EmailSendRequestDto {

    @Schema(description = "아이디 찾기 시 필요" , example = "김철수")
    private String usrNm;

    @Schema(description = "비밀번호 찾기 시 필요" , example = "ustoId")
    private String usrId;

    @NotBlank(message = "이메일을 입력해주세요")
    @Schema(example = "example@usto.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String target;
}
