package com.usto.api.item.disposal.domain.model;

import com.usto.api.item.common.model.ApprStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 처분 신청서 도메인 모델
 */
@Getter
@Builder
public class DisposalMaster {

    private UUID dispMId;                      // 처분ID
    private String aplyUsrId;                  // 등록자ID
    private LocalDate aplyAt;                  // 처분등록일자
    private DisposalArrangementType dispType;  // 처분방식
    private String apprUsrId;                  // 확정자ID
    private LocalDate dispApprAt;                  // 처분확정일자
    private ApprStatus apprSts;                // 승인상태
    private String orgCd;
    private String delYn;
    private LocalDateTime delAt;
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;

    /**
     * 처분 정보 수정
     */
    public void updateInfo(DisposalArrangementType dispType, LocalDate aplyAt) {
        this.dispType = dispType;
        this.aplyAt = aplyAt;
    }

    /**
     * 승인 요청
     */
    public void requestApproval() {
        this.apprSts = ApprStatus.REQUEST;
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return "Y".equals(this.delYn);
    }

    //승인
    public void confirmApproval(String userId) {
        this.apprSts = ApprStatus.APPROVED; //불용 확정 처라
        this.apprUsrId = userId;
        this.dispApprAt = LocalDate.now();
    }
    //반려
    public void rejectApproval(String userId) {
        this.apprSts = ApprStatus.REJECTED; //불용 반려 처라
        this.apprUsrId = userId;
        this.dispApprAt = LocalDate.now();
    }
}