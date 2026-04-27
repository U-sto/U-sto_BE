package com.usto.api.ai.forecast.presentation.dto.response;

import java.util.UUID;

public record AiForecastGetResponse(
        UUID id,
        String name
){}
