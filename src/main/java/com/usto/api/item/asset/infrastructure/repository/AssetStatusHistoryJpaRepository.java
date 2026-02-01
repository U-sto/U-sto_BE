package com.usto.api.item.asset.infrastructure.repository;

import com.usto.api.item.asset.infrastructure.entity.ItemAssetStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssetStatusHistoryJpaRepository extends JpaRepository<ItemAssetStatusHistoryEntity, UUID> {
    List<ItemAssetStatusHistoryEntity> findByItmNoAndOrgCdOrderByApprAtDesc(String itmNo, String orgCd);
}