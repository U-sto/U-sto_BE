package com.usto.api.item.acquisition.infrastructure.mapper;

import com.usto.api.item.acquisition.domain.model.Acquisition;
import com.usto.api.item.acquisition.infrastructure.entity.ItemAcquisitionEntity;

public final class AcquisitionMapper {
    private AcquisitionMapper() {}

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
                .operSts(entity.getOperSts())
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
                // BaseTime
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
                .operSts(domain.getOperSts())
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