package com.usto.api.ai.forecast.infrastructure.adapter;

import com.usto.api.ai.forecast.domain.model.Forecast;
import com.usto.api.ai.forecast.domain.repository.ForecastRepository;
import com.usto.api.ai.forecast.infrastructure.entity.ForecastJpaEntity;
import com.usto.api.ai.forecast.infrastructure.mapper.ForecastMapper;
import com.usto.api.ai.forecast.infrastructure.repository.ForecastJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForecastRepositoryAdapter implements ForecastRepository {

    private final ForecastJpaRepository jpaRepository;

    @Override
    public void save(Forecast forecast) {
        ForecastJpaEntity entity = ForecastMapper.toEntity(forecast);
        jpaRepository.save(entity);
    }

    @Override
    public Forecast findById(UUID forecastId) {
        ForecastJpaEntity entity = jpaRepository.findById(forecastId)
                .orElseThrow(() -> new EntityNotFoundException("해당 예측 정보를 찾을 수 없습니다. id: " + forecastId));
        return ForecastMapper.toDomain(entity);
    }

    @Override
    public List<UUID> findByUsrId(String username) {
        return jpaRepository.findIdsByUserId(username);    }

    @Override
    public void delete(UUID forecastId) {
        jpaRepository.deleteById(forecastId);
    }
}
