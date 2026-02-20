package com.usto.api.item.asset.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import com.usto.api.item.common.model.OperStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TB_ITEM007 - 물품상태이력
 */
@Entity
@Table(name = "TB_ITEM007")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE TB_ITEM007 SET del_yn = 'Y', del_at = NOW() WHERE item_his_id = ?")
@Where(clause = "DEL_YN = 'N'")
public class ItemAssetStatusHistoryEntity extends BaseTimeEntity {

    @Id
    @Column(name = "ITEM_HIS_ID", columnDefinition = "BINARY(16)")
    private UUID itemHisId;

    @Column(name = "ITM_NO", nullable = false, length = 10, columnDefinition = "char(10)")
    private String itmNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "PREV_STS", length = 30)
    private OperStatus prevSts;

    @Enumerated(EnumType.STRING)
    @Column(name = "NEW_STS", nullable = false, length = 30)
    private OperStatus newSts;

    @Column(name = "CHG_RSN", length = 200)
    private String chgRsn;

    @Column(name = "REQ_USR_ID", nullable = false, length = 30)
    private String reqUsrId;

    @Column(name = "REQ_AT", nullable = false)
    private LocalDate reqAt;

    @Column(name = "APPR_USR_ID", length = 30)
    private String apprUsrId;

    @Column(name = "APPR_AT")
    private LocalDate apprAt;

    @Column(name = "ORG_CD", nullable = false, length = 7, columnDefinition = "char(7)")
    private String orgCd;

    @Builder.Default
    @Column(name = "DEL_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String delYn = "N";

    @Column(name = "DEL_AT")
    private LocalDateTime delAt;
}