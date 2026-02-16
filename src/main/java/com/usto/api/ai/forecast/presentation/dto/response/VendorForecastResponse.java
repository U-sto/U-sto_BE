package com.usto.api.ai.forecast.presentation.dto.response;

public record VendorForecastResponse(
        String tsJson,
        String matrixJson,
        String recoJson
) {}
