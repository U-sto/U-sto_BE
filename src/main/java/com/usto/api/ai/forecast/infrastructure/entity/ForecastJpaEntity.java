package com.usto.api.ai.forecast.infrastructure.entity;

import com.usto.api.ai.forecast.domain.model.RiskLevel;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "TB_FC001M")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ForecastJpaEntity {

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

    @Column(name = "DEPT_CD", length = 5)
    private String deptCd;

    @Column(name = "G2B_D_NM", length = 300)
    private String g2bDNm;

    @Column(name = "TS_JSON", columnDefinition = "LONGTEXT")
    private String timeSeriesJson;

    @Column(name = "MATRIX_JSON", columnDefinition = "LONGTEXT")
    private String matrixJson;

    @Column(name = "RECO_JSON", columnDefinition = "LONGTEXT")
    private String recommendationJson;

    @Column(name = "ORG_CD", length = 7, nullable = false)
    private String orgCd;

    @PrePersist
    public void prePersist() {
        if (this.forecastId == null) {
            this.forecastId = UUID.randomUUID();
        }
    }
}
