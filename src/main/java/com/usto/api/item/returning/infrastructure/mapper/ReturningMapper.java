package com.usto.api.item.returning.infrastructure.mapper;

import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.returning.domain.model.ReturningDetail;
import com.usto.api.item.returning.domain.model.ReturningMaster;
import com.usto.api.item.returning.domain.model.ReturningReason;
import com.usto.api.item.returning.infrastructure.entity.ItemReturningDetailEntity;
import com.usto.api.item.returning.infrastructure.entity.ItemReturningMasterEntity;

import java.time.LocalDate;
import java.util.UUID;

public final class ReturningMapper {
    private ReturningMapper() {}

    // ===== Master =====

    /**
     * 신규 ReturningMaster 도메인 생성 (기존 ReturningMaster.create() 이동)
     */
    public static ReturningMaster toMasterDomain(
            String aplyUsrId,
            LocalDate aplyAt,
            ItemStatus itemSts,
            ReturningReason rtrnRsn,
            String orgCd
    ) {
        return ReturningMaster.builder()
                .rtrnMId(UUID.randomUUID())
                .aplyUsrId(aplyUsrId)
                .aplyAt(aplyAt)
                .itemSts(itemSts)
                .rtrnRsn(rtrnRsn)
                .apprSts(ApprStatus.WAIT)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }

    /**
     * Entity → Domain (Master)
     */
    public static ReturningMaster toMasterDomain(ItemReturningMasterEntity entity) {
        return ReturningMaster.builder()
                .rtrnMId(entity.getRtrnMId())
                .aplyUsrId(entity.getAplyUsrId())
                .aplyAt(entity.getAplyAt())
                .itemSts(entity.getItemSts())
                .rtrnRsn(entity.getRtrnRsn())
                .apprUsrId(entity.getApprUsrId())
                .rtrnApprAt(entity.getRtrnApprAt())
                .apprSts(entity.getApprSts())
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
     * Domain → Entity (Master)
     */
    public static ItemReturningMasterEntity toMasterEntity(ReturningMaster domain) {
        return ItemReturningMasterEntity.builder()
                .rtrnMId(domain.getRtrnMId())
                .aplyUsrId(domain.getAplyUsrId())
                .aplyAt(domain.getAplyAt())
                .itemSts(domain.getItemSts())
                .rtrnRsn(domain.getRtrnRsn())
                .apprUsrId(domain.getApprUsrId())
                .rtrnApprAt(domain.getRtrnApprAt())
                .apprSts(domain.getApprSts())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .delAt(domain.getDelAt())
                .build();
    }

    // ===== Detail =====

    /**
     * 신규 ReturningDetail 도메인 생성 (기존 ReturningDetail.create() 이동)
     */
    public static ReturningDetail toDetailDomain(
            UUID rtrnMId,
            String itmNo,
            String deptCd,
            String orgCd
    ) {
        return ReturningDetail.builder()
                .rtrnDId(UUID.randomUUID())
                .rtrnMId(rtrnMId)
                .itmNo(itmNo)
                .deptCd(deptCd)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }

    /**
     * Entity → Domain (Detail)
     */
    public static ReturningDetail toDetailDomain(ItemReturningDetailEntity entity) {
        return ReturningDetail.builder()
                .rtrnDId(entity.getRtrnDId())
                .rtrnMId(entity.getRtrnMId())
                .itmNo(entity.getItmNo())
                .deptCd(entity.getDeptCd())
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
     * Domain → Entity (Detail)
     */
    public static ItemReturningDetailEntity toDetailEntity(ReturningDetail domain) {
        return ItemReturningDetailEntity.builder()
                .rtrnDId(domain.getRtrnDId())
                .rtrnMId(domain.getRtrnMId())
                .itmNo(domain.getItmNo())
                .deptCd(domain.getDeptCd())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .delAt(domain.getDelAt())
                .build();
    }
}