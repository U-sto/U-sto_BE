package com.usto.api.item.disuse.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.disuse.domain.model.DisuseReason;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TB_ITEM005M - 물품불용기본
 */
@Entity
@Table(name = "TB_ITEM005M")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE TB_ITEM005M SET del_yn = 'Y', del_at = NOW() WHERE dsu_m_id = ?")
@Where(clause = "DEL_YN = 'N'")
public class ItemDisuseMasterEntity extends BaseTimeEntity {

    @Id
    @Column(name = "DSU_M_ID", columnDefinition = "BINARY(16)")
    private UUID dsuMId;

    @Column(name = "APLY_USR_ID", nullable = false, length = 30)
    private String aplyUsrId;

    @Column(name = "APLY_AT", nullable = false)
    private LocalDate aplyAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "ITEM_STS", nullable = false, length = 30)
    private ItemStatus itemSts;

    @Enumerated(EnumType.STRING)
    @Column(name = "CHG_RSN", nullable = false, length = 30)
    private DisuseReason dsuRsn;

    @Column(name = "APPR_USR_ID", length = 30)
    private String apprUsrId;

    @Column(name = "DSU_APPR_AT")
    private LocalDate dsuApprAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "APPR_STS", nullable = false, length = 30)
    @Builder.Default
    private ApprStatus apprSts = ApprStatus.WAIT;

    @Column(name = "ORG_CD", nullable = false, length = 7, columnDefinition = "char(7)")
    private String orgCd;

    @Builder.Default
    @Column(name = "DEL_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String delYn = "N";

    @Column(name = "DEL_AT")
    private LocalDateTime delAt;
}