package com.usto.api.item.operation.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TB_ITEM003M - 물품운용기본
 */
@Entity
@Table(name = "TB_ITEM003M")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE TB_ITEM003M SET del_yn = 'Y', del_at = NOW() WHERE oper_m_id = ?")
@Where(clause = "DEL_YN = 'N'")
public class ItemOperationMasterEntity extends BaseTimeEntity {

    @Id
    @Column(name = "OPER_M_ID", columnDefinition = "BINARY(16)")
    private UUID operMId;

    @Column(name = "APLY_USR_ID", nullable = false, length = 30)
    private String aplyUsrId;

    @Column(name = "APLY_AT", nullable = false)
    private LocalDate aplyAt;

    @Column(name = "DEPT_CD", nullable = false, length = 5, columnDefinition = "char(5)")
    private String deptCd;

    @Enumerated(EnumType.STRING)
    @Column(name = "ITEM_STS", nullable = false, length = 30)
    private ItemStatus itemSts;

    @Column(name = "APPR_USR_ID", length = 30)
    private String apprUsrId;

    @Column(name = "OPER_APPR_AT")
    private LocalDate operApprAt;

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