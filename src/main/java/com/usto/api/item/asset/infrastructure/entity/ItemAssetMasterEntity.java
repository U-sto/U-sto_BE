package com.usto.api.item.asset.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TB_ITEM002M - 물품대장기본 (취득 묶음 단위 헤더)
 */
@Entity
@Table(name = "TB_ITEM002M")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE TB_ITEM002M SET del_yn = 'Y', del_at = NOW() WHERE acq_id = ?")
@Where(clause = "DEL_YN = 'N'")
public class ItemAssetMasterEntity extends BaseTimeEntity {

    @Id
    @Column(name = "ACQ_ID", columnDefinition = "BINARY(16)")
    private UUID acqId;        // 취득ID (TB_ITEM001M과 1:1 관계)

    @Column(name = "G2B_D_CD", nullable = false, length = 8, columnDefinition = "char(8)")
    private String g2bDCd;

    @Column(name = "QTY", nullable = false)
    private Integer qty;       // 수량 (처분 시 감소)

    @Column(name = "ACQ_AT", nullable = false)
    private LocalDate acqAt;   // 취득일자 (스냅샷)

    @Column(name = "ARRG_AT")
    private LocalDate arrgAt;  // 정리일자 (스냅샷)

    @Column(name = "ACQ_ARRG_TY", nullable = false, length = 20)
    private String acqArrgTy;  // 정리구분

    @Column(name = "ORG_CD", nullable = false, length = 7, columnDefinition = "char(7)")
    private String orgCd;

    @Builder.Default
    @Column(name = "DEL_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String delYn = "N";

    @Column(name = "DEL_AT")
    private LocalDateTime delAt;
}