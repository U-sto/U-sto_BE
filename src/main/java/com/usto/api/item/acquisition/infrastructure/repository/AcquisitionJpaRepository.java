package com.usto.api.item.acquisition.infrastructure.repository;

import com.querydsl.core.types.Projections;
import com.usto.api.item.acquisition.infrastructure.entity.ItemAcquisitionEntity;
import com.usto.api.item.returning.infrastructure.entity.ItemReturningMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AcquisitionJpaRepository extends JpaRepository<ItemAcquisitionEntity, UUID> {
    Optional<ItemAcquisitionEntity> findByAcqIdAndOrgCd(UUID id, String orgCd);
}