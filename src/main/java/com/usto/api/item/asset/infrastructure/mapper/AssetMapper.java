package com.usto.api.item.asset.infrastructure.mapper;

import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetMaster;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetDetailEntity;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetMasterEntity;

public final class AssetMapper {
    private AssetMapper() {}

    /**
     * Entity → Domain
     */
    public static Asset toDomain(ItemAssetDetailEntity entity) {
        return Asset.builder()
                .itmNo(entity.getItmNo())
                .acqId(entity.getAcqId())
                .g2bDCd(entity.getG2bDCd())
                .deptCd(entity.getDeptCd())
                .operSts(entity.getOperSts())
                .acqUpr(entity.getAcqUpr())
                .drbYr(entity.getDrbYr())
                .rmk(entity.getRmk())
                .printYn(entity.getPrintYn())
                .orgCd(entity.getOrgCd())
                .delYn(entity.getDelYn())
                .delAt(entity.getDelAt())
                .creBy(entity.getCreBy())
                .creAt(entity.getCreAt())
                .updBy(entity.getUpdBy())
                .updAt(entity.getUpdAt())
                .build();
    }

    /**
     * Domain → Entity
     */
    public static ItemAssetDetailEntity toEntity(Asset domain) {
        return ItemAssetDetailEntity.builder()
                .itmNo(domain.getItmNo())
                .acqId(domain.getAcqId())
                .g2bDCd(domain.getG2bDCd())
                .deptCd(domain.getDeptCd())
                .operSts(domain.getOperSts())
                .acqUpr(domain.getAcqUpr())
                .drbYr(domain.getDrbYr())
                .rmk(domain.getRmk())
                .printYn(domain.getPrintYn())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .delAt(domain.getDelAt())
                .build();
    }


    /**
     * Entity → Domain
     */
    public static AssetMaster toMasterDomain(ItemAssetMasterEntity entity) {
        return AssetMaster.builder()
                .acqId(entity.getAcqId())
                .g2bDCd(entity.getG2bDCd())
                .qty(entity.getQty())
                .acqAt(entity.getAcqAt())
                .arrgAt(entity.getArrgAt())
                .orgCd(entity.getOrgCd())
                .delYn(entity.getDelYn())
                .build();
    }

    /**
     * Domain → Entity
     */
    public static ItemAssetMasterEntity toMasterEntity(AssetMaster domain) {
        return ItemAssetMasterEntity.builder()
                .acqId(domain.getAcqId())
                .g2bDCd(domain.getG2bDCd())
                .qty(domain.getQty())
                .acqAt(domain.getAcqAt())
                .arrgAt(domain.getArrgAt())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .build();
    }
}