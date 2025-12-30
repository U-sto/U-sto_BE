package com.usto.api.user.domain.model;

import com.usto.api.common.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class Verification extends BaseTime {

    private Long id;
    private VerificationType type;
    private VerificationPurpose purpose;
    private String target;
    private String code;
    private LocalDateTime expiresAt;
    private boolean isVerified;
    private LocalDateTime verifiedAt;

    // 재발송 갱신(코드/만료 갱신 + 성공상태 초기화)
    public void renew(String newCode, LocalDateTime newExpiresAt) {
        this.code = newCode;
        this.expiresAt = newExpiresAt;
        this.isVerified = false;
        this.verifiedAt = null;
    }

    // 인증 성공 처리
    public void verify(LocalDateTime now) {
        this.isVerified = true;
        this.verifiedAt = now;
    }
}
