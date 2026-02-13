package com.usto.api.item.operation.infrastructure.mapper;

import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.operation.domain.model.OperationDetail;
import com.usto.api.item.operation.domain.model.OperationMaster;
import com.usto.api.item.operation.infrastructure.entity.ItemOperationDetailEntity;
import com.usto.api.item.operation.infrastructure.entity.ItemOperationMasterEntity;

import java.time.LocalDate;
import java.util.UUID;

public final class OperationMapper {
    private OperationMapper() {}

    // ===== Master =====

    /**
     * 신규 OperationMaster 도메인 생성
     */
    public static OperationMaster toMasterDomain(
            String aplyUsrId,
            LocalDate aplyAt,
            String deptCd,
            ItemStatus itemSts,
            String orgCd
    ) {
        return OperationMaster.builder()
                .operMId(UUID.randomUUID())
                .aplyUsrId(aplyUsrId)
                .aplyAt(aplyAt)
                .deptCd(deptCd)
                .itemSts(itemSts)
                .apprSts(ApprStatus.WAIT)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }

    /**
     * Entity → Domain (Master)
     */
    public static OperationMaster toMasterDomain(ItemOperationMasterEntity entity) {
        return OperationMaster.builder()
                .operMId(entity.getOperMId())
                .aplyUsrId(entity.getAplyUsrId())
                .aplyAt(entity.getAplyAt())
                .deptCd(entity.getDeptCd())
                .itemSts(entity.getItemSts())
                .apprUsrId(entity.getApprUsrId())
                .operApprAt(entity.getOperApprAt())
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
    public static ItemOperationMasterEntity toMasterEntity(OperationMaster domain) {
        return ItemOperationMasterEntity.builder()
                .operMId(domain.getOperMId())
                .aplyUsrId(domain.getAplyUsrId())
                .aplyAt(domain.getAplyAt())
                .deptCd(domain.getDeptCd())
                .itemSts(domain.getItemSts())
                .apprUsrId(domain.getApprUsrId())
                .operApprAt(domain.getOperApprAt())
                .apprSts(domain.getApprSts())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .delAt(domain.getDelAt())
                .build();
    }

    // ===== Detail =====

    /**
     * 신규 OperationDetail 도메인 생성
     */
    public static OperationDetail toDetailDomain(
            UUID operMId,
            String itmNo,
            String orgCd
    ) {
        return OperationDetail.builder()
                .operDId(UUID.randomUUID())
                .operMId(operMId)
                .itmNo(itmNo)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }

    /**
     * Entity → Domain (Detail)
     */
    public static OperationDetail toDetailDomain(ItemOperationDetailEntity entity) {
        return OperationDetail.builder()
                .operDId(entity.getOperDId())
                .operMId(entity.getOperMId())
                .itmNo(entity.getItmNo())
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
    public static ItemOperationDetailEntity toDetailEntity(OperationDetail domain) {
        return ItemOperationDetailEntity.builder()
                .operDId(domain.getOperDId())
                .operMId(domain.getOperMId())
                .itmNo(domain.getItmNo())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .delAt(domain.getDelAt())
                .build();
    }
}