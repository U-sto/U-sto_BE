package com.usto.api.item.returning.infrastructure.mapper;

import com.usto.api.item.returning.domain.model.ReturningDetail;
import com.usto.api.item.returning.domain.model.ReturningMaster;
import com.usto.api.item.returning.infrastructure.entity.ItemReturningDetailEntity;
import com.usto.api.item.returning.infrastructure.entity.ItemReturningMasterEntity;

public final class ReturningMapper {
    private ReturningMapper() {}

    // ===== Master =====

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