package com.usto.api.item.acquisition.infrastructure.mapper;

import com.usto.api.item.acquisition.domain.model.AcqArrangementType;
import com.usto.api.item.acquisition.domain.model.Acquisition;
import com.usto.api.item.acquisition.infrastructure.entity.ItemAcquisitionEntity;
import com.usto.api.item.common.model.ApprStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class AcquisitionMapper {
    private AcquisitionMapper() {}

    /**
     * 신규 Acquisition 도메인 생성 (기존 Acquisition.create() 메서드 이동)
     * Domain -> Domain
     */
    public static Acquisition toDomain(
            String g2bDCd,
            LocalDate acqAt,
            BigDecimal acqUpr,
            String deptCd,
            String drbYr,
            Integer acqQty,
            AcqArrangementType arrgTy,
            String rmk,
            String aplyUsrId,
            String orgCd
    ) {
        return Acquisition.builder()
                .acqId(UUID.randomUUID())
                .g2bDCd(g2bDCd)
                .acqAt(acqAt)
                .acqUpr(acqUpr)
                .deptCd(deptCd)
                .drbYr(drbYr)
                .acqQty(acqQty)
                .arrgTy(arrgTy)
                .rmk(rmk)
                .apprSts(ApprStatus.WAIT) // 초기 승인상태
                .aplyUsrId(aplyUsrId)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }

    /**
     * Entity → Domain 변환
     */
    public static Acquisition toDomain(ItemAcquisitionEntity entity) {
        return Acquisition.builder()
                .acqId(entity.getAcqId())
                .g2bDCd(entity.getG2bDCd())
                .acqAt(entity.getAcqAt())
                .acqUpr(entity.getAcqUpr())
                .deptCd(entity.getDeptCd())
                .drbYr(entity.getDrbYr())
                .acqQty(entity.getAcqQty())
                .arrgTy(entity.getArrgTy())
                .apprSts(entity.getApprSts())
                .rmk(entity.getRmk())
                .aplyUsrId(entity.getAplyUsrId())
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
     * Domain → Entity 변환
     */
    public static ItemAcquisitionEntity toEntity(Acquisition domain) {
        return ItemAcquisitionEntity.builder()
                .acqId(domain.getAcqId())
                .g2bDCd(domain.getG2bDCd())
                .acqAt(domain.getAcqAt())
                .acqUpr(domain.getAcqUpr())
                .deptCd(domain.getDeptCd())
                .drbYr(domain.getDrbYr())
                .acqQty(domain.getAcqQty())
                .arrgTy(domain.getArrgTy())
                .apprSts(domain.getApprSts())
                .rmk(domain.getRmk())
                .aplyUsrId(domain.getAplyUsrId())
                .apprUsrId(domain.getApprUsrId())
                .apprAt(domain.getApprAt())
                .orgCd(domain.getOrgCd())
                .delYn(domain.getDelYn())
                .delAt(domain.getDelAt())
                .build();
    }
}