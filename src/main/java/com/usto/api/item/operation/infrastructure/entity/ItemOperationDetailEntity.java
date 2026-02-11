package com.usto.api.item.operation.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TB_ITEM003D - 물품운용상세
 */
@Entity
@Table(name = "TB_ITEM003D")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE TB_ITEM003D SET del_yn = 'Y', del_at = NOW() WHERE oper_d_id = ?")
@Where(clause = "DEL_YN = 'N'")
public class ItemOperationDetailEntity extends BaseTimeEntity {

    @Id
    @Column(name = "OPER_D_ID", columnDefinition = "BINARY(16)")
    private UUID operDId;

    @Column(name = "OPER_M_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID operMId;

    @Column(name = "ITM_NO", nullable = false, length = 10, columnDefinition = "char(10)")
    private String itmNo;

    @Column(name = "ORG_CD", nullable = false, length = 7, columnDefinition = "char(7)")
    private String orgCd;

    @Builder.Default
    @Column(name = "DEL_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String delYn = "N";

    @Column(name = "DEL_AT")
    private LocalDateTime delAt;
}
