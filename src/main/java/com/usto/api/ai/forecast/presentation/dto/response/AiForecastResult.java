package com.usto.api.ai.forecast.presentation.dto.response;

public record AiForecastResult(
        String tsJson,
        String matrixJson,
        String recoJson
) {}
