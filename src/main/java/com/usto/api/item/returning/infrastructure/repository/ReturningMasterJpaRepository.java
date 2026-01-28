package com.usto.api.item.returning.infrastructure.repository;

import com.usto.api.item.returning.infrastructure.entity.ItemReturningMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReturningMasterJpaRepository extends JpaRepository<ItemReturningMasterEntity, UUID> {
    Optional<ItemReturningMasterEntity> findByRtrnMIdAndOrgCd(UUID rtrnMId, String orgCd);
}