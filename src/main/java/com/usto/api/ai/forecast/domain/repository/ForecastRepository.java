package com.usto.api.ai.forecast.domain.repository;

import com.usto.api.ai.forecast.domain.model.Forecast;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ForecastRepository {
    void save(Forecast forecast);

    Forecast findById(@Valid UUID forecastId);

    List<UUID> findByUsrId(String username);

    void delete(@Valid UUID forecastId);
}
