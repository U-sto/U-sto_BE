package com.usto.api.item.asset.infrastructure.mapper;

import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetStatusHistory;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetEntity;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetId;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetStatusHistoryEntity;
import com.usto.api.item.common.model.OperStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class AssetMapper {
    private AssetMapper() {}

    /**
     * 신규 Asset 도메인 생성 (기존 Asset.create() 메서드)
     * Domain -> Domain
     */
    public static Asset toDomain(
            String itmNo, UUID acqId, String g2bDCd,
            String deptCd, BigDecimal acqUpr, String drbYr, String orgCd, String rmk
    ) {
        return Asset.builder()
                .itmNo(itmNo)
                .acqId(acqId)
                .g2bDCd(g2bDCd)
                .deptCd(deptCd)
                .operSts(OperStatus.OPER)
                .acqUpr(acqUpr)
                .drbYr(drbYr)
                .orgCd(orgCd)
                .rmk(rmk)
                .printYn("N")
                .delYn("N")
                .build();
    }

    /**
     * Entity → Domain
     */
    public static Asset toDomain(ItemAssetEntity entity) {
        return Asset.builder()
                .itmNo(entity.getItemId().getItmNo())
                .orgCd(entity.getItemId().getOrgCd())
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
    public static ItemAssetEntity toEntity(Asset domain) {
        return ItemAssetEntity.builder()
                .itemId(new ItemAssetId(domain.getItmNo(), domain.getOrgCd()))
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
     * 신규 AssetStatusHistory 도메인 생성 (기존 create() 메서드 이동)
     */
    public static AssetStatusHistory toStatusHistoryDomain(
            String itmNo,
            OperStatus prevSts,
            OperStatus newSts,
            String chgRsn,
            String reqUsrId,
            LocalDate reqAt,
            String apprUsrId,
            LocalDate apprAt,
            String orgCd
    ) {
        return AssetStatusHistory.builder()
                .itemHisId(UUID.randomUUID())
                .itmNo(itmNo)
                .prevSts(prevSts)
                .newSts(newSts)
                .chgRsn(chgRsn)
                .reqUsrId(reqUsrId)
                .reqAt(reqAt)
                .apprUsrId(apprUsrId)
                .apprAt(apprAt)
                .orgCd(orgCd)
                .delYn("N")
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