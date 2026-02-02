package com.usto.api.item.disuse.infrastructure.repository;

import com.usto.api.item.disuse.infrastructure.entity.ItemDisuseMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DisuseMasterJpaRepository extends JpaRepository<ItemDisuseMasterEntity, UUID> {
    Optional<ItemDisuseMasterEntity> findByDsuMIdAndOrgCd(UUID dsuMId, String orgCd);
}