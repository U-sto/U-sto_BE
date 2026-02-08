package com.usto.api.item.disuse.domain.model;

import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 불용 신청서 도메인 모델
 */
@Getter
@Builder
public class DisuseMaster {

    private UUID dsuMId;            // 불용ID
    private String aplyUsrId;       // 등록자ID
    private LocalDate aplyAt;       // 불용등록일자
    private ItemStatus itemSts;     // 물품상태
    private DisuseReason dsuRsn;    // 불용사유
    private String apprUsrId;       // 확정자ID
    private LocalDate dsuApprAt;    // 불용확정일자
    private ApprStatus apprSts;     // 승인상태
    private String orgCd;
    private String delYn;
    private LocalDateTime delAt;
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;

    /**
     * 불용 정보 수정
     */
    public void updateInfo(ItemStatus itemSts, DisuseReason dsuRsn) {
        this.itemSts = itemSts;
        this.dsuRsn = dsuRsn;
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

    public void confirmApproval(String userId) {
        this.apprSts = ApprStatus.APPROVED; //불용 확정 처라
        this.apprUsrId = userId;
        this.dsuApprAt = LocalDate.now();
    }

    public void rejectApproval(String userId) {
        this.apprSts = ApprStatus.REJECTED; //불용 반려 처라
        this.apprUsrId = userId;
        this.dsuApprAt = LocalDate.now();
    }
}