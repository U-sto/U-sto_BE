package com.usto.api.ai.forecast.domain.repository;

import com.usto.api.ai.forecast.domain.model.Forecast;
import com.usto.api.ai.forecast.infrastructure.entity.ForecastJpaEntity;
import jakarta.validation.Valid;

import java.util.UUID;

public interface ForecastRepository {
    void save(Forecast forecast);

    Forecast findById(@Valid UUID forecastId);
}
