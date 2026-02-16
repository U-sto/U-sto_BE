package com.usto.api.ai.forecast.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Forecast {
    private final UUID forecastId;
    private final String userId;
    private final Short analysisYear;
    private final Byte semester;
    private final RiskLevel riskLevel;
    private final String message;
    private final String deptCd;
    private final String g2bDNm;
    private final String tsJson;
    private final String matrixJson;
    private final String recoJson;
    private final String orgCode;
}
