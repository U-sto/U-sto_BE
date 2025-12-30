package com.usto.api.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 이 클래스를 상속받는 모든 엔티티(테이블)는
 cre_by(생성자) , 'upd_by'(수정자)
 'cre_at'(생성일)과 'upd_at'(수정일) 컬럼을 자동으로 갖게 됌
 */
@Setter
@Getter
@MappedSuperclass // 상속 전용(직접적인 사용하진 않는 엔티티에)
@EntityListeners(AuditingEntityListener.class) // 날짜 변화 감지
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BaseTimeEntity {

    //생성자ID
    @CreatedBy
    @Column(name="cre_by", updatable = false, length = 30)
    private String creBy;

    //생성일자
    @CreatedDate
    @Column(name = "cre_at", updatable = false) //수정 불가
    private LocalDateTime creAt;

    //수정자ID
    @LastModifiedBy
    @Column(name="upd_by" , length = 30)
    private String updBy;

    //수정일자
    @LastModifiedDate // 자동 갱신
    @Column(name = "upd_at")
    private LocalDateTime updAt;

    //테스트할 때를 위한
    @PrePersist
    void prePersist() {
        if (creAt == null) creAt = LocalDateTime.now();
        if (creBy == null) creBy = "SYSTEM";
    }
}