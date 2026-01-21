package com.usto.api.item.acquisition.infrastructure.repository;

import com.usto.api.item.acquisition.infrastructure.entity.ItemAcquisitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AcquisitionJpaRepository extends JpaRepository<ItemAcquisitionEntity, UUID> {
}