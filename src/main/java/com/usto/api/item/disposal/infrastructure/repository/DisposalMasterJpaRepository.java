package com.usto.api.item.disposal.infrastructure.repository;

import com.usto.api.item.disposal.infrastructure.entity.ItemDisposalMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DisposalMasterJpaRepository extends JpaRepository<ItemDisposalMasterEntity, UUID> {
    Optional<ItemDisposalMasterEntity> findByDispMIdAndOrgCd(UUID dispMId, String orgCd);
}