package com.usto.api.ai.forecast.infrastructure.entity;

import com.usto.api.ai.forecast.domain.model.RiskLevel;
import com.usto.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_FC001M")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE TB_FC001M SET del_yn = 'Y', del_at = NOW() WHERE FC_M_ID = ?")
@Where(clause = "DEL_YN = 'N'")
public class ForecastJpaEntity extends BaseTimeEntity {

    @Id
    @Column(name = "FC_M_ID", columnDefinition = "BINARY(16)")
    private UUID forecastId;

    @Column(name = "USR_ID", length = 50, nullable = false)
    private String userId;

    @Column(name = "ANALYSIS_YEAR", nullable = false)
    private Short analysisYear;

    @Column(name = "SEMESTER", nullable = false)
    private Byte semester;

    @Enumerated(EnumType.STRING)
    @Column(name = "RISK_LEVEL", length = 10, nullable = false)
    private RiskLevel riskLevel; // LOW, MEDIUM, HIGH

    @Column(name = "MESSAGE", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "DEPT_CD", length = 5, columnDefinition = "char(5)")
    private String deptCd;

    @Column(name = "G2B_D_NM", length = 300)
    private String g2bDNm;

    @Lob
    @Column(name = "SUM_JSON", columnDefinition = "LONGTEXT")
    private String summaryJson;

    @Lob
    @Column(name = "TS_JSON", columnDefinition = "LONGTEXT")
    private String timeSeriesJson;

    @Lob
    @Column(name = "MATRIX_JSON", columnDefinition = "LONGTEXT")
    private String matrixJson;

    @Lob
    @Column(name = "RECO_JSON", columnDefinition = "LONGTEXT")
    private String recommendationJson;

    @Column(name = "ORG_CD", length = 7, nullable = false,columnDefinition = "CHAR(7)")
    private String orgCd;

    @Builder.Default
    @Column(name = "DEL_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String delYn = "N";   // 삭제여부

    @Column(name = "DEL_AT")
    private LocalDateTime delAt;  // 삭제일시
}
