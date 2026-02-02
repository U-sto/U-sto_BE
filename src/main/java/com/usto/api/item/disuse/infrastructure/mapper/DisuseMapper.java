package com.usto.api.item.disuse.infrastructure.mapper;

import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.disuse.domain.model.DisuseDetail;
import com.usto.api.item.disuse.domain.model.DisuseMaster;
import com.usto.api.item.disuse.domain.model.DisuseReason;
import com.usto.api.item.disuse.infrastructure.entity.ItemDisuseDetailEntity;
import com.usto.api.item.disuse.infrastructure.entity.ItemDisuseMasterEntity;

import java.time.LocalDate;
import java.util.UUID;

public final class DisuseMapper {
    private DisuseMapper() {}

    // ===== Master =====

    /**
     * 신규 DisuseMaster 도메인 생성
     */
    public static DisuseMaster toMasterDomain(
            String aplyUsrId,
            LocalDate aplyAt,
            ItemStatus itemSts,
            DisuseReason dsuRsn,
            String orgCd
    ) {
        return DisuseMaster.builder()
                .dsuMId(UUID.randomUUID())
                .aplyUsrId(aplyUsrId)
                .aplyAt(aplyAt)
                .itemSts(itemSts)
                .dsuRsn(dsuRsn)
                .apprSts(ApprStatus.WAIT)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }

    /**
     * Entity → Domain (Master)
     */
    public static DisuseMaster toMasterDomain(ItemDisuseMasterEntity entity) {
        return DisuseMaster.builder()
                .dsuMId(entity.getDsuMId())
                .aplyUsrId(entity.getAplyUsrId())
                .aplyAt(entity.getAplyAt())
                .itemSts(entity.getItemSts())
                .dsuRsn(entity.getDsuRsn())
                .apprUsrId(entity.getApprUsrId())
                .dsuApprAt(entity.getDsuApprAt())
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
    public static ItemDisuseMasterEntity toMasterEntity(DisuseMaster domain) {
        return ItemDisuseMasterEntity.builder()
                .dsuMId(domain.getDsuMId())
                .aplyUsrId(domain.getAplyUsrId())
                .aplyAt(domain.getAplyAt())
                .itemSts(domain.getItemSts())
                .dsuRsn(domain.getDsuRsn())
                .apprUsrId(domain.getApprUsrId())
                .dsuApprAt(domain.getDsuApprAt())
                .apprSts(domain.getApprSts())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .delAt(domain.getDelAt())
                .build();
    }

    // ===== Detail =====

    /**
     * 신규 DisuseDetail 도메인 생성
     */
    public static DisuseDetail toDetailDomain(
            UUID dsuMId,
            String itmNo,
            String deptCd,
            String orgCd
    ) {
        return DisuseDetail.builder()
                .dsuDId(UUID.randomUUID())
                .dsuMId(dsuMId)
                .itmNo(itmNo)
                .deptCd(deptCd)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }

    /**
     * Entity → Domain (Detail)
     */
    public static DisuseDetail toDetailDomain(ItemDisuseDetailEntity entity) {
        return DisuseDetail.builder()
                .dsuDId(entity.getDsuDId())
                .dsuMId(entity.getDsuMId())
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
    public static ItemDisuseDetailEntity toDetailEntity(DisuseDetail domain) {
        return ItemDisuseDetailEntity.builder()
                .dsuDId(domain.getDsuDId())
                .dsuMId(domain.getDsuMId())
                .itmNo(domain.getItmNo())
                .deptCd(domain.getDeptCd())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .delAt(domain.getDelAt())
                .build();
    }
}