package com.usto.api.item.disposal.infrastructure.mapper;

import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.disposal.domain.model.DisposalArrangementType;
import com.usto.api.item.disposal.domain.model.DisposalDetail;
import com.usto.api.item.disposal.domain.model.DisposalMaster;
import com.usto.api.item.disposal.infrastructure.entity.ItemDisposalDetailEntity;
import com.usto.api.item.disposal.infrastructure.entity.ItemDisposalMasterEntity;
import com.usto.api.item.disuse.domain.model.DisuseReason;

import java.time.LocalDate;
import java.util.UUID;

public final class DisposalMapper {
    private DisposalMapper() {}

    // ===== Master =====

    /**
     * 신규 DisposalMaster 도메인 생성
     */
    public static DisposalMaster toMasterDomain(
            String aplyUsrId,
            DisposalArrangementType dispType,
            LocalDate dispAt,
            String orgCd
    ) {
        return DisposalMaster.builder()
                .dispMId(UUID.randomUUID())
                .aplyUsrId(aplyUsrId)
                .dispType(dispType)
                .dispAt(dispAt)
                .apprSts(ApprStatus.WAIT)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }

    /**
     * Entity → Domain (Master)
     */
    public static DisposalMaster toMasterDomain(ItemDisposalMasterEntity entity) {
        return DisposalMaster.builder()
                .dispMId(entity.getDispMId())
                .aplyUsrId(entity.getAplyUsrId())
                .dispType(entity.getDispType())
                .dispAt(entity.getDispAt())
                .apprUsrId(entity.getApprUsrId())
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
    public static ItemDisposalMasterEntity toMasterEntity(DisposalMaster domain) {
        return ItemDisposalMasterEntity.builder()
                .dispMId(domain.getDispMId())
                .aplyUsrId(domain.getAplyUsrId())
                .dispType(domain.getDispType())
                .dispAt(domain.getDispAt())
                .apprUsrId(domain.getApprUsrId())
                .apprSts(domain.getApprSts())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .delAt(domain.getDelAt())
                .build();
    }

    // ===== Detail =====

    /**
     * 신규 DisposalDetail 도메인 생성
     * - 불용 테이블의 물품상태와 사유를 받아서 생성
     */
    public static DisposalDetail toDetailDomain(
            UUID dispMId,
            String itmNo,
            ItemStatus itemSts,
            DisuseReason chgRsn,
            String orgCd
    ) {
        return DisposalDetail.builder()
                .dispDId(UUID.randomUUID())
                .dispMId(dispMId)
                .itmNo(itmNo)
                .itemSts(itemSts)
                .chgRsn(chgRsn)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }

    /**
     * Entity → Domain (Detail)
     */
    public static DisposalDetail toDetailDomain(ItemDisposalDetailEntity entity) {
        return DisposalDetail.builder()
                .dispDId(entity.getDispDId())
                .dispMId(entity.getDispMId())
                .itmNo(entity.getItmNo())
                .itemSts(entity.getItemSts())
                .chgRsn(entity.getChgRsn())
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
    public static ItemDisposalDetailEntity toDetailEntity(DisposalDetail domain) {
        return ItemDisposalDetailEntity.builder()
                .dispDId(domain.getDispDId())
                .dispMId(domain.getDispMId())
                .itmNo(domain.getItmNo())
                .itemSts(domain.getItemSts())
                .chgRsn(domain.getChgRsn())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .delAt(domain.getDelAt())
                .build();
    }
}