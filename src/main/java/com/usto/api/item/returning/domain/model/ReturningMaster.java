package com.usto.api.item.returning.domain.model;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
     * 신규 반납 신청서 생성
     */
    public static ReturningMaster create(
            String aplyUsrId,
            LocalDate aplyAt,
            ItemStatus itemSts,
            ReturningReason rtrnRsn,
            String orgCd
    ) {
        validateAplyAt(aplyAt);

        return ReturningMaster.builder()
                .rtrnMId(UUID.randomUUID())
                .aplyUsrId(aplyUsrId)
                .aplyAt(aplyAt)
                .itemSts(itemSts)
                .rtrnRsn(rtrnRsn)
                .apprSts(ApprStatus.WAIT)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }

    /**
     * 반납 정보 수정 (WAIT 상태만 가능)
     */
    public void updateInfo(ItemStatus itemSts, ReturningReason rtrnRsn) {
        validateModifiable();
        this.itemSts = itemSts;
        this.rtrnRsn = rtrnRsn;
    }

    /**
     * 승인 요청
     */
    public void requestApproval() {
        if (this.apprSts != ApprStatus.WAIT) {
            throw new BusinessException("승인요청이 가능한 상태가 아닙니다.");
        }
        this.apprSts = ApprStatus.REQUEST;
    }

    /**
     * 승인 요청 취소 가능 여부
     */
    public void validateCancellable() {
        if (this.apprSts != ApprStatus.REQUEST) {
            throw new BusinessException("승인요청 중인 상태만 취소할 수 있습니다.");
        }
    }


    /**
     * TODO: 승인 처리
     */

    /**
     * TODO: 반려 처리
     */


    /**
     * 수정/삭제 가능 여부
     */
    public void validateModifiable() {
        if (this.apprSts != ApprStatus.WAIT) {
            throw new BusinessException("승인 요청 중이거나 확정된 데이터는 수정/삭제할 수 없습니다.");
        }
    }

    /**
     * 반납일자 검증
     */
    private static void validateAplyAt(LocalDate aplyAt) {
        if (aplyAt == null || aplyAt.isAfter(LocalDate.now(ZoneId.of("Asia/Seoul")))) {
            throw new BusinessException("반납일자는 미래 날짜를 선택할 수 없습니다.");
        }
    }

    /**
     * 조직 소유권 검증
     */
    public void validateOwnership(String requestOrgCd) {
        if (!this.orgCd.equals(requestOrgCd)) {
            throw new BusinessException("해당 조직의 데이터가 아닙니다.");
        }
    }

    public boolean isDeleted() {
        return "Y".equals(this.delYn);
    }
}