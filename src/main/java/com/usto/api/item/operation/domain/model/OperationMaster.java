package com.usto.api.item.operation.domain.model;

import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 운용 신청서 도메인 모델 (TB_ITEM003M)
 */
@Getter
@Builder
public class OperationMaster {

    private UUID operMId;               // 운용ID
    private String aplyUsrId;           // 등록자ID
    private LocalDate aplyAt;           // 운용(등록)일자
    private String deptCd;              // 운용부서코드 (스냅샷)
    private ItemStatus itemSts;         // 물품상태 (NEW/USED/REPAIRABLE/SCRAP)
    private String apprUsrId;           // 확정자ID
    private LocalDate operApprAt;       // 운용확정일자
    private ApprStatus apprSts;         // 승인상태
    private String orgCd;               // 조직코드
    private String delYn;
    private LocalDateTime delAt;
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;

    /**
     * 운용 정보 수정
     */
    public void updateInfo(String deptCd, ItemStatus itemSts) {
        this.deptCd = deptCd;
        this.itemSts = itemSts;
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

    /**
     * 승인 확정
     */
    public void confirmApproval(String userId) {
        this.apprUsrId = userId;
        this.apprSts = ApprStatus.APPROVED;
        this.operApprAt = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));    }

    /**
     * 반려
     */
    public void rejectApproval(String userId) {
        this.apprUsrId = userId;
        this.apprSts = ApprStatus.REJECTED;
        this.operApprAt = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));    }
    }
}