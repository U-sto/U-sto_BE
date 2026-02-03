package com.usto.api.item.returning.domain.service;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.returning.domain.model.ReturningMaster;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Returning 도메인의 비즈니스 정책
 */
@Component
public class ReturningPolicy {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    /**
     * 조직 소유권 검증
     */
    public void validateOwnership(ReturningMaster master, String requestOrgCd) {
        if (!master.getOrgCd().equals(requestOrgCd)) {
            throw new BusinessException("해당 조직의 데이터가 아닙니다.");
        }
    }

    /**
     * 수정/삭제 가능 여부 검증
     */
    public void validateModifiable(ReturningMaster master) {
        if (master.getApprSts() != ApprStatus.WAIT) {
            throw new BusinessException("승인 요청 중이거나 확정된 데이터는 수정/삭제할 수 없습니다.");
        }
    }

    /**
     * 승인 요청 가능 여부 검증
     */
    public void validateRequestable(ReturningMaster master) {
        if (master.getApprSts() != ApprStatus.WAIT) {
            throw new BusinessException("승인요청이 가능한 상태가 아닙니다.");
        }
    }

    /**
     * 승인 취소 가능 여부 검증
     */
    public void validateCancellable(ReturningMaster master) {
        if (master.getApprSts() != ApprStatus.REQUEST) {
            throw new BusinessException("승인요청 중인 상태만 취소할 수 있습니다.");
        }
    }

    /**
     * 반납일자 검증
     */
    public void validateAplyAt(LocalDate aplyAt) {
        if (aplyAt == null || aplyAt.isAfter(LocalDate.now(KOREA_ZONE))) {
            throw new BusinessException("반납일자는 미래 날짜를 선택할 수 없습니다.");
        }
    }

    // 반납 확정 가능 여부 검증
    public void validateApprovable(ReturningMaster master) {
        if (master.getApprSts() != ApprStatus.REQUEST) {
            throw new BusinessException("승인요청 중인 상태만 확정할 수 있습니다.");
        }
    }
}