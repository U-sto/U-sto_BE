package com.usto.api.item.asset.infrastructure.repository;

import com.usto.api.item.asset.domain.model.AssetMaster;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssetMasterJpaRepository extends JpaRepository<ItemAssetMasterEntity, UUID> {
}