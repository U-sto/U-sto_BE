package com.usto.api.ai.forecast.infrastructure.mapper;

import com.usto.api.ai.forecast.domain.model.Forecast;
import com.usto.api.ai.forecast.domain.model.RiskLevel;
import com.usto.api.ai.forecast.infrastructure.entity.ForecastJpaEntity;

import java.util.UUID;

public class ForecastMapper {
    // Overload 1: Data(Inputs) -> Domain (For creating new forecast result)
    public static Forecast toDomain(String userId, Short year, Byte semester, RiskLevel risk, String msg, String org) {
        return Forecast.builder()
                .forecastId(UUID.randomUUID())
                .userId(userId)
                .analysisYear(year)
                .semester(semester)
                .riskLevel(risk)
                .message(msg)
                .orgCode(org)
                .build();
    }

    // Overload 2: Entity -> Domain (For reading from DB)
    public static Forecast toDomain(ForecastJpaEntity entity) {
        return Forecast.builder()
                .forecastId(entity.getForecastId())
                .userId(entity.getUserId())
                .analysisYear(entity.getAnalysisYear())
                .semester(entity.getSemester())
                .riskLevel(entity.getRiskLevel())
                .message(entity.getMessage())
                .deptCd(entity.getDeptCd())
                .g2bDNm(entity.getG2bDNm())
                .tsJson(entity.getTimeSeriesJson())
                .matrixJson(entity.getMatrixJson())
                .recoJson(entity.getRecommendationJson())
                .orgCode(entity.getOrgCd())
                .build();
    }

    // Domain -> Entity
    public static ForecastJpaEntity toEntity(Forecast domain) {
        return ForecastJpaEntity.builder()
                .forecastId(domain.getForecastId())
                .userId(domain.getUserId())
                .analysisYear(domain.getAnalysisYear())
                .semester(domain.getSemester())
                .riskLevel(domain.getRiskLevel())
                .message(domain.getMessage())
                .deptCd(domain.getDeptCd())
                .g2bDNm(domain.getG2bDNm())
                .timeSeriesJson(domain.getTsJson())
                .matrixJson(domain.getMatrixJson())
                .recommendationJson(domain.getRecoJson())
                .orgCd(domain.getOrgCode())
                .build();
    }
}
