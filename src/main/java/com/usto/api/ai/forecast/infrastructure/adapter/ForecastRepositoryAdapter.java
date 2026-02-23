package com.usto.api.ai.forecast.infrastructure.adapter;

import com.usto.api.ai.forecast.domain.model.Forecast;
import com.usto.api.ai.forecast.domain.repository.ForecastRepository;
import com.usto.api.ai.forecast.infrastructure.entity.ForecastJpaEntity;
import com.usto.api.ai.forecast.infrastructure.mapper.ForecastMapper;
import com.usto.api.ai.forecast.infrastructure.repository.ForecastJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForecastRepositoryAdapter implements ForecastRepository {

    private final ForecastJpaRepository jpaRepository;

    @Override
    public void save(Forecast forecast) {
        ForecastJpaEntity entity = ForecastMapper.toEntity(forecast);
        jpaRepository.save(entity);
    }
}
