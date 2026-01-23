package com.usto.api.item.asset.infrastructure.repository;

import com.usto.api.item.asset.infrastructure.entity.ItemAssetMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AssetMasterJpaRepository extends JpaRepository<ItemAssetMasterEntity, UUID> {
}