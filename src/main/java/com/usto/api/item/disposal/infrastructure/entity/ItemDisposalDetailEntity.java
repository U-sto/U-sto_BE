package com.usto.api.item.disposal.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.disuse.domain.model.DisuseReason;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TB_ITEM006D - 물품처분상세
 * - 불용기본 테이블의 물품상태와 사유를 가져옴
 */
@Entity
@Table(name = "TB_ITEM006D")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE TB_ITEM006D SET del_yn = 'Y', del_at = NOW() WHERE disp_d_id = ?")
@Where(clause = "DEL_YN = 'N'")
public class ItemDisposalDetailEntity extends BaseTimeEntity {

    @Id
    @Column(name = "DISP_D_ID", columnDefinition = "BINARY(16)")
    private UUID dispDId;

    @Column(name = "DISP_M_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID dispMId;

    @Column(name = "ITM_NO", nullable = false, length = 10, columnDefinition = "char(10)")
    private String itmNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "ITEM_STS", nullable = false, length = 30)
    private ItemStatus itemSts;  // 불용기본 테이블에서 가져옴

    @Enumerated(EnumType.STRING)
    @Column(name = "CHG_RSN", nullable = false, length = 30)
    private DisuseReason chgRsn;  // 불용기본 테이블에서 가져옴

    @Column(name = "ORG_CD", nullable = false, length = 7, columnDefinition = "char(7)")
    private String orgCd;

    @Builder.Default
    @Column(name = "DEL_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String delYn = "N";

    @Column(name = "DEL_AT")
    private LocalDateTime delAt;
}