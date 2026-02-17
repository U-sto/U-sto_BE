package com.usto.api.ai.forecast.presentation.dto.request;

public record AiForecastRequest(
        int analysisYear, //conditions.year
        int semester,
        String riskLevel,     // LOW/MEDIUM/HIGH
        String deptCd,        // nullable
        String g2bDNm,        // nullable
        String message //prompt
) {}
