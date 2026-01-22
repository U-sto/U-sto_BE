package com.usto.api.item.asset.infrastructure.mapper;

import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetMaster;
import com.usto.api.item.asset.domain.model.AssetStatusHistory;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetDetailEntity;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetDetailId;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetMasterEntity;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetStatusHistoryEntity;

public final class AssetMapper {
    private AssetMapper() {}

    /**
     * Entity → Domain
     */
    public static Asset toDomain(ItemAssetDetailEntity entity) {
        return Asset.builder()
                .itmNo(entity.getItemId().getItmNo())  // id 객체에서 꺼내기
                .orgCd(entity.getItemId().getOrgCd())  // id 객체에서 꺼내기
                .acqId(entity.getAcqId())
                .g2bDCd(entity.getG2bDCd())
                .deptCd(entity.getDeptCd())
                .operSts(entity.getOperSts())
                .acqUpr(entity.getAcqUpr())
                .drbYr(entity.getDrbYr())
                .rmk(entity.getRmk())
                .printYn(entity.getPrintYn())
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
                .itemId(new ItemAssetDetailId(domain.getItmNo(), domain.getOrgCd())) // ID 객체 생성
                .acqId(domain.getAcqId())
                .g2bDCd(domain.getG2bDCd())
                .deptCd(domain.getDeptCd())
                .operSts(domain.getOperSts())
                .acqUpr(domain.getAcqUpr())
                .drbYr(domain.getDrbYr())
                .rmk(domain.getRmk())
                .printYn(domain.getPrintYn())
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
                .acqArrgTy(entity.getAcqArrgTy())
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
                .acqArrgTy(domain.getAcqArrgTy())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .build();
    }


    /**
     * Entity → Domain (AssetStatusHistory)
     */
    public static AssetStatusHistory toStatusHistoryDomain(ItemAssetStatusHistoryEntity entity) {
        return AssetStatusHistory.builder()
                .itemHisId(entity.getItemHisId())
                .itmNo(entity.getItmNo())
                .prevSts(entity.getPrevSts())
                .newSts(entity.getNewSts())
                .chgRsn(entity.getChgRsn())
                .reqUsrId(entity.getReqUsrId())
                .reqAt(entity.getReqAt())
                .apprUsrId(entity.getApprUsrId())
                .apprAt(entity.getApprAt())
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
     * Domain → Entity (AssetStatusHistory)
     */
    public static ItemAssetStatusHistoryEntity toStatusHistoryEntity(AssetStatusHistory domain) {
        return ItemAssetStatusHistoryEntity.builder()
                .itemHisId(domain.getItemHisId())
                .itmNo(domain.getItmNo())
                .prevSts(domain.getPrevSts())
                .newSts(domain.getNewSts())
                .chgRsn(domain.getChgRsn())
                .reqUsrId(domain.getReqUsrId())
                .reqAt(domain.getReqAt())
                .apprUsrId(domain.getApprUsrId())
                .apprAt(domain.getApprAt())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .delAt(domain.getDelAt())
                .build();
    }
}