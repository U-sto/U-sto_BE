package com.usto.api.item.operation.infrastructure.repository;

import com.usto.api.item.operation.infrastructure.entity.ItemOperationMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OperationMasterJpaRepository extends JpaRepository<ItemOperationMasterEntity, UUID> {
    Optional<ItemOperationMasterEntity> findByOperMIdAndOrgCd(UUID operMId, String orgCd);
}