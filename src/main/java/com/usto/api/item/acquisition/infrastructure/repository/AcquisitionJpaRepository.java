package com.usto.api.item.acquisition.infrastructure.repository;

import com.usto.api.item.acquisition.infrastructure.entity.ItemAcquisitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcquisitionJpaRepository extends JpaRepository<ItemAcquisitionEntity, String> {
}