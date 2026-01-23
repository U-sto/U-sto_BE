package com.usto.api.item.asset.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import com.usto.api.item.common.model.OperStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TB_ITEM002D - 물품대장상세 (개별 물품 단위)
 */
@Entity
@Table(name = "TB_ITEM002D")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE TB_ITEM002D SET del_yn = 'Y', del_at = NOW() WHERE itm_no = ? AND org_cd = ?")
@Where(clause = "DEL_YN = 'N'")
public class ItemAssetDetailEntity extends BaseTimeEntity {

    @EmbeddedId
    private ItemAssetDetailId itemId; // PK = ITEM_NO + ORG_CD

    @Column(name = "ACQ_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID acqId;    // 취득ID

    @Column(name = "G2B_D_CD", nullable = false, length = 8, columnDefinition = "char(8)")
    private String g2bDCd;

    @Column(name = "DEPT_CD", nullable = false, length = 5, columnDefinition = "char(5)")
    private String deptCd;

    @Enumerated(EnumType.STRING)
    @Column(name = "OPER_STS", nullable = false, length = 30)
    private OperStatus operSts; // ACQ/OPER/RTN/DSU

    @Column(name = "ACQ_UPR", nullable = false, precision = 20)
    private BigDecimal acqUpr;  // 수정 가능

    @Column(name = "DRB_YR", nullable = false, length = 20)
    private String drbYr;       // 수정 가능

    @Column(name = "RMK", length = 500)
    private String rmk;         // 수정 가능

    @Builder.Default
    @Column(name = "PRINT_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String printYn = "N";

    @Builder.Default
    @Column(name = "DEL_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String delYn = "N";

    @Column(name = "DEL_AT")
    private LocalDateTime delAt;
}