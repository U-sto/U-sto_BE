package com.usto.api.ai.forecast.presentation.dto.response;

public record AiForecastResponse(
        String tsJson,
        String matrixJson,
        String recoJson
) {}
