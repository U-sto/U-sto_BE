package com.usto.api.ai.forecast.domain.repository;

import com.usto.api.ai.forecast.domain.model.Forecast;

public interface ForecastRepository {
    void save(Forecast forecast);
}
