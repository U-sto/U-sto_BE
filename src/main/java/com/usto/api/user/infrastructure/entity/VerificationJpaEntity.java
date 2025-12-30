/*
 * Verification 엔티티
 * - 역할: 이메일/전화번호 인증번호와 상태를 저장하는 테이블의 도메인 모델입니다.
 * - 주요 필드: target(이메일/전화), code(인증번호), type(EMAIL/PHONE), expiresAt(만료), isVerified(성공여부)
 * - 메서드: renew(코드/만료 갱신 및 인증상태 초기화), verify(성공 표시)
 */
package com.usto.api.user.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.model.VerificationType;
import jakarta.persistence.*;
import lombok.*;
import com.usto.api.common.utils.YesNoConverter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;

@Entity
@Table(name = "TB_VERIF001M")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class VerificationJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto_increment
    @Column(name="VERIF_ID")
    private Long id;

    // 인증 대상 (이메일 주소 또는 전화번호)
    @Column(name="TARGET",nullable = false)
    private String target;

    // 인증 타입 (EMAIL, PHONE)
    @Enumerated(EnumType.STRING)
    @Column(name="VERIF_TYPE",length = 20, nullable = false)
    private VerificationType type;

    // 인증 목적
    @Enumerated(EnumType.STRING)
    @Column(name="VERIF_PURPOSE",length = 100,nullable = false)
    private VerificationPurpose purpose;

    // 인증 코드 (예: "1234")
    @Column(name="VERIF_CODE",length = 20,nullable = false)
    private String code;

    // 만료 시간 (생성 후 5분)
    @Column(name="EXPIRES_AT" ,nullable = false)
    private LocalDateTime expiresAt;

    // 인증 성공 여부
    @Column(name = "IS_VERIFIED",nullable = false, columnDefinition = "CHAR(1)")
    @Convert(converter = YesNoConverter.class)
    private boolean isVerified;

    //인증 성공 시각
    @Column(name = "VERIFIED_AT")
    private LocalDateTime verifiedAt;
}