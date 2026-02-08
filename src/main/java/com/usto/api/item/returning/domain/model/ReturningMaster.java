package com.usto.api.item.returning.domain.model;

import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 반납 신청서 도메인 모델 (TB_ITEM003M)
 */
@Getter
@Builder
public class ReturningMaster {

    private UUID rtrnMId;               // 반납ID
    private String aplyUsrId;           // 등록자ID
    private LocalDate aplyAt;           // 반납(등록)일자
    private ItemStatus itemSts;         // 물품상태 (NEW/USED/REPAIRABLE/SCRAP)
    private ReturningReason rtrnRsn;    // 반납사유 (Enum)
    private String apprUsrId;           // 확정자ID
    private LocalDate rtrnApprAt;       // 반납확정일자
    private ApprStatus apprSts;         // 승인상태
    private String orgCd;               // 조직코드
    private String delYn;
    private LocalDateTime delAt;
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;

    /**
     * 반납 정보 수정
     */
    public void updateInfo(ItemStatus itemSts, ReturningReason rtrnRsn) {
        this.itemSts = itemSts;
        this.rtrnRsn = rtrnRsn;
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

    //반납 확정
    public void confirmApproval(String userId) {
        this.apprSts = ApprStatus.APPROVED; //반납 확정 처라
        this.apprUsrId = userId;
        this.rtrnApprAt = LocalDate.now();
    }

    //반납 신청 반려
    public void rejectApproval(String userId) {
        this.apprSts = ApprStatus.REJECTED; //반납 반려 처라
        this.apprUsrId = userId;
        this.rtrnApprAt = LocalDate.now();
    }
}