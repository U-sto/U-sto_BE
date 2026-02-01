package com.usto.api.g2b.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "TB_G2B_USRFUL")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class G2bUsrfulListJpaEntity {

    @Id
    @Column(name = "G2B_M_CD", length = 20, nullable = false)
    private String g2bMCd; // 물품분류번호 (PK)

    @Column(name = "G2B_M_NM", nullable = false)
    private String g2bMNm; // 품명

    @Column(name = "DRB_YR",nullable = false)
    private String drbYr; // 내용연수

}
