package com.usto.api.item.disposal.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.disposal.domain.model.DisposalArrangementType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TB_ITEM005M - 물품처분기본
 */
@Entity
@Table(name = "TB_ITEM005M")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE TB_ITEM005M SET del_yn = 'Y', del_at = NOW() WHERE disp_m_id = ?")
@Where(clause = "DEL_YN = 'N'")
public class ItemDisposalMasterEntity extends BaseTimeEntity {

    @Id
    @Column(name = "DISP_M_ID", columnDefinition = "BINARY(16)")
    private UUID dispMId;

    @Column(name = "APLY_USR_ID", nullable = false, length = 30)
    private String aplyUsrId;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISP_TYPE", nullable = false, length = 30)
    private DisposalArrangementType dispType;

    @Column(name = "DISP_AT", nullable = false)
    private LocalDate dispAt;

    @Column(name = "APPR_USR_ID", length = 30)
    private String apprUsrId;

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