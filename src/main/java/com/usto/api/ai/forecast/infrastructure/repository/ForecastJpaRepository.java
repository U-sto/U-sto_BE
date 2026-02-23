package com.usto.api.ai.forecast.infrastructure.repository;

import com.usto.api.ai.forecast.infrastructure.entity.ForecastJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ForecastJpaRepository extends JpaRepository<ForecastJpaEntity, UUID> {

    ForecastJpaEntity save(ForecastJpaEntity entity);
}
