package com.usto.api.item.asset.domain.service;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.common.model.OperStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * Asset 도메인의 복잡한 비즈니스 정책
 * - 여러 객체를 함께 사용하는 복잡한 검증 로직
 * - 단일 도메인 객체를 넘어서는 정책
 */
@Component
public class AssetPolicy {

    /**
     * 물품 수정 가능 여부 검증
     */
    public void validateUpdate(Asset asset, String requestOrgCd) {
        // 조직 소유권 검증
        if (!asset.getOrgCd().equals(requestOrgCd)) {
            throw new BusinessException("해당 조직의 데이터가 아닙니다.");
        }

        // 삭제 여부 검증
        if (asset.isDeleted()) {
            throw new BusinessException("삭제된 물품은 수정할 수 없습니다.");
        }

        // 상태 검증
        if (asset.getOperSts() == OperStatus.DSU) {
            throw new BusinessException("불용 처리된 물품은 수정할 수 없습니다.");
        }
    }

    /**
     * 취득단가 유효성 검증
     */
    public void validateAcquisitionPrice(BigDecimal acqUpr) {
        if (acqUpr == null || acqUpr.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("취득 단가는 0원 이상이어야 합니다.");
        }
    }

    /**
     * 반납 가능 여부 검증
     */
    public void validateReturn(Asset asset) {
        if (asset.isDeleted()) {
            throw new BusinessException("삭제된 물품은 반납할 수 없습니다.");
        }

        if (asset.getOperSts() == OperStatus.DSU) {
            throw new BusinessException("불용 처리된 물품은 반납할 수 없습니다.");
        }

        if (!StringUtils.hasText(asset.getDeptCd()) || "NONE".equals(asset.getDeptCd())) {
            throw new BusinessException("배정된 부서가 없습니다.");
        }
    }

    /**
     * 부서 배정 가능 여부 검증
     */
    public void validateAssignment(Asset asset, String deptCd) {
        if (asset.getOperSts() != OperStatus.RTN) {
            throw new BusinessException("반납 상태일 때만 부서 배정이 가능합니다.");
        }

        if (!StringUtils.hasText(deptCd) || "NONE".equals(deptCd)) {
            throw new BusinessException("유효한 부서 코드가 필요합니다.");
        }
    }
}