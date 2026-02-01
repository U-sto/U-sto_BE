package com.usto.api.item.acquisition.domain.service;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.acquisition.domain.model.Acquisition;
import com.usto.api.item.common.model.ApprStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Acquisition 도메인의 비즈니스 정책
 */
@Component
public class AcquisitionPolicy {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    /**
     * 조직 소유권 검증
     */
    public void validateOwnership(Acquisition acquisition, String requestOrgCd) {
        if (!acquisition.getOrgCd().equals(requestOrgCd)) {
            throw new BusinessException("해당 조직의 데이터가 아닙니다.");
        }
    }

    /**
     * 수정/삭제 가능 여부 검증
     */
    public void validateModifiable(Acquisition acquisition) {
        if (acquisition.getApprSts() == ApprStatus.REQUEST) {
            throw new BusinessException("승인 요청 중인 데이터는 수정/삭제할 수 없습니다.");
        }
        if (acquisition.getApprSts() == ApprStatus.APPROVED) {
            throw new BusinessException("승인 확정된 데이터는 수정/삭제할 수 없습니다.");
        }
        if (acquisition.getApprSts() == ApprStatus.REJECTED) {
            throw new BusinessException("반려된 데이터는 수정/삭제할 수 없습니다.");
        }
    }

    /**
     * 승인 요청 가능 여부 검증
     */
    public void validateRequestable(Acquisition acquisition) {
        if (acquisition.getApprSts() != ApprStatus.WAIT) {
            throw new BusinessException("승인 요청이 가능한 상태가 아닙니다.");
        }
    }

    /**
     * 승인 취소 가능 여부 검증
     */
    public void validateCancellable(Acquisition acquisition) {
        if (acquisition.getApprSts() != ApprStatus.REQUEST) {
            throw new BusinessException("승인 요청 중인 상태만 취소할 수 있습니다.");
        }
    }

    /**
     * 승인 확정 가능 여부 검증
     */
    public void validateApprovable(Acquisition acquisition) {
        if (acquisition.getApprSts() != ApprStatus.REQUEST) {
            throw new BusinessException("승인 요청 상태에서만 확정할 수 있습니다.");
        }
    }

    /**
     * 취득일자 검증
     */
    public void validateAcquisitionDate(LocalDate acqAt) {
        if (acqAt.isAfter(LocalDate.now(KOREA_ZONE))) {
            throw new BusinessException("취득일자는 현재 날짜 이후일 수 없습니다.");
        }
    }
}