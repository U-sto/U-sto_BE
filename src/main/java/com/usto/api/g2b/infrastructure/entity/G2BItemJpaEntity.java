package com.usto.api.g2b.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * @class G2BItemJpaEntity
 * @desc 조달청 세부 품목 엔티티 (TB_G2B001D)
 */
@Entity
@Table(name = "TB_G2B001D")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class G2BItemJpaEntity {
    @Id
    // columnDefinition을 추가해서 DB의 CHAR 타입과 맞춤
    @Column(name = "G2B_D_CD", length = 8, columnDefinition = "char(8)")
    private String g2bDCd;

    @Column(name = "G2B_M_CD", length = 8, nullable = false, columnDefinition = "char(8)")
    private String g2bMCd;

    @Column(name = "G2B_D_NM", length = 300, nullable = false)
    private String g2bDNm;

    @Column(name = "G2B_UPR", precision = 20, scale = 0, nullable = false)
    private BigDecimal g2bUpr;
}