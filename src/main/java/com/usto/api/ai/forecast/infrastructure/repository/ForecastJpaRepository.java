package com.usto.api.ai.forecast.infrastructure.repository;

import com.usto.api.ai.forecast.infrastructure.entity.ForecastJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ForecastJpaRepository extends JpaRepository<ForecastJpaEntity, UUID> {

    ForecastJpaEntity save(ForecastJpaEntity entity);

    @Query("SELECT e.forecastId FROM ForecastJpaEntity e WHERE e.userId = :userId")
    List<UUID> findIdsByUserId(@Param("userId") String username);
}
