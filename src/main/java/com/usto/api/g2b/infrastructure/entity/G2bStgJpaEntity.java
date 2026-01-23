package com.usto.api.g2b.infrastructure.entity;

import com.usto.api.g2b.domain.model.G2bStg;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_G2B_STG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class G2bStgJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STG_ID")
    private Long id;

    @Column(name = "G2B_M_CD", length = 8, nullable = false, columnDefinition = "char(8)")
    private String g2bMCd;

    @Column(name = "G2B_M_NM", length = 300, nullable = false)
    private String g2bMNm;

    @Column(name = "G2B_D_CD", length = 8, columnDefinition = "char(8)")
    private String g2bDCd;

    @Column(name = "G2B_D_NM", length = 300, nullable = false)
    private String g2bDNm;

    @Column(name = "G2B_UPR", precision = 20, scale = 0, nullable = false)
    private BigDecimal g2bUpr;

}
