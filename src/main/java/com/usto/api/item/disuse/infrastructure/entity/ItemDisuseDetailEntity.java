package com.usto.api.item.disuse.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TB_ITEM004D - 물품불용상세
 */
@Entity
@Table(name = "TB_ITEM005D")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE TB_ITEM005D SET del_yn = 'Y', del_at = NOW() WHERE dsu_d_id = ?")
@Where(clause = "DEL_YN = 'N'")
public class ItemDisuseDetailEntity extends BaseTimeEntity {

    @Id
    @Column(name = "DSU_D_ID", columnDefinition = "BINARY(16)")
    private UUID dsuDId;

    @Column(name = "DSU_M_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID dsuMId;

    @Column(name = "ITM_NO", nullable = false, length = 10, columnDefinition = "char(10)")
    private String itmNo;

    @Column(name = "DEPT_CD", nullable = false, length = 5, columnDefinition = "char(5)")
    private String deptCd;  // 스냅샷

    @Column(name = "ORG_CD", nullable = false, length = 7, columnDefinition = "char(7)")
    private String orgCd;

    @Builder.Default
    @Column(name = "DEL_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String delYn = "N";

    @Column(name = "DEL_AT")
    private LocalDateTime delAt;
}