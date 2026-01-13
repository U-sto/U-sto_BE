package com.usto.api.user.domain.model;

import com.usto.api.common.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true)
@Getter
public class Verification extends BaseTime {

    private Long id;
    private VerificationType type;
    private VerificationPurpose purpose;
    private String target;
    private String code;
    private LocalDateTime expiresAt;
    private boolean isVerified;
    private LocalDateTime verifiedAt;

    //만료여부 확인
    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(this.expiresAt);
    }

    //인증 코드 일치 여부
    public boolean isCodeMatching(String inputCode) {
        return this. code. equals(inputCode);
    }

    //인증 가능 여부(만료 안됨 + 아직 검증 안됨)
    public boolean canVerify(LocalDateTime now) {
        return !isExpired(now) && !this.isVerified;
    }

    //재발송을 위한 갱신
    public Verification  renew(String newCode, LocalDateTime newExpiresAt) {
        return this.toBuilder()
                .code(newCode)
                .expiresAt(newExpiresAt)
                .isVerified(false)
                .verifiedAt(null)
                .build();
    }

    //인증 성공 처리
    public Verification  verify(LocalDateTime now) {
        if (!canVerify(now)) {
            throw new IllegalStateException("인증할 수 없는 상태입니다");
        }
        return this.toBuilder()
                .isVerified(true)
                .verifiedAt(now)
                .build();
    }
}
