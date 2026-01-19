package com.usto.api.g2b.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_G2B_STG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class G2bStgJpaEntity {

    @Id
    @Column(name = "G2B_D_CD", length = 8, nullable = false)
    private String g2bDCd;

    @Column(name = "G2B_UPR", precision = 20, scale = 0)
    private BigDecimal g2bUpr;

}
