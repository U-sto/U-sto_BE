package com.usto.api.item.acquisition.domain.model;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.OperStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Getter
@Builder
public class Acquisition {

    // 식별자
    private UUID acqId;  // UUID

    // 물품 정보
    private String g2bDCd;
    private LocalDate acqAt;
    private BigDecimal acqUpr;
    private String deptCd;
    private OperStatus operSts;
    private String drbYr;
    private Integer acqQty;
    private AcqArrangementType arrgTy;

    // 승인 정보
    private ApprStatus apprSts;
    private String aplyUsrId;
    private String apprUsrId;
    private LocalDate apprAt;

    // 기타
    private String rmk;
    private String orgCd;
    private String delYn;
    private LocalDateTime delAt;

    // BaseTime 필드
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;

    // 한국 표준시 (서울)
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    // ===== 비즈니스 로직 메서드 =====

    /**
     * 신규 취득 생성 (UUID 자동 생성)
     */
    public static Acquisition create(
            String g2bDCd,
            LocalDate acqAt,
            BigDecimal acqUpr,
            String deptCd,
            OperStatus operSts,
            String drbYr,
            Integer acqQty,
            AcqArrangementType arrgTy,
            String rmk,
            String aplyUsrId,
            String orgCd
    ) {
        return Acquisition.builder()
                .acqId(UUID.randomUUID())  // UUID 생성
                .g2bDCd(g2bDCd)
                .acqAt(acqAt)
                .acqUpr(acqUpr)
                .deptCd(deptCd)
                .operSts(OperStatus.ACQ)   // 초기 운용상태
                .drbYr(drbYr)
                .acqQty(acqQty)
                .arrgTy(arrgTy)
                .rmk(rmk)
                .apprSts(ApprStatus.WAIT)  // 초기 승인상태
                .aplyUsrId(aplyUsrId)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }

    /**
     * 취득 정보 수정 (WAIT/REJECTED 상태만 가능)
     */
    public void updateInfo(
            String g2bDCd,
            LocalDate acqAt,
            BigDecimal acqUpr,
            String deptCd,
            OperStatus operSts,
            String drbYr,
            Integer acqQty,
            AcqArrangementType arrgTy,
            String rmk
    ) {
        validateModifiable();

        this.g2bDCd = g2bDCd;
        this.acqAt = acqAt;
        this.acqUpr = acqUpr;
        this.deptCd = deptCd;
        this.operSts = operSts;
        this.drbYr = drbYr;
        this.acqQty = acqQty;
        this.arrgTy = arrgTy;
        this.rmk = rmk;
    }

    /**
     * 승인 상태 변경
     */
    public void changeStatus(ApprStatus newStatus) {
        this.apprSts = newStatus;

        // 확정 시 확정일자 자동 설정
        if (newStatus == ApprStatus.APPROVED) {
            this.apprAt = LocalDate.now(KOREA_ZONE);
        }
    }

    /**
     * 승인 요청 (WAIT/REJECTED → REQUEST)
     */
    public void requestApproval() {
        if (this.apprSts != ApprStatus.WAIT && this.apprSts != ApprStatus.REJECTED) {
            throw new BusinessException("승인요청이 가능한 상태가 아닙니다.");
        }
        changeStatus(ApprStatus.REQUEST);
    }

    /**
     * 승인 요청 취소 가능 여부 체크
     */
    public void validateCancellable() {
        if (this.apprSts != ApprStatus.REQUEST) {
            throw new BusinessException("승인요청 중인 상태만 취소할 수 있습니다.");
        }
    }

    /**
     * 수정/삭제 가능 여부 체크
     */
    public void validateModifiable() {
        if (this.apprSts == ApprStatus.REQUEST || this.apprSts == ApprStatus.APPROVED) {
            throw new BusinessException("승인 요청 중이거나 확정된 데이터는 수정/삭제할 수 없습니다.");
        }
    }

    /**
     * 조직 소유권 검증: 다른 조직의 데이터 접근 방지
     */
    public void validateOwnership(String requestOrgCd) {
        if (!this.orgCd.equals(requestOrgCd)) {
            throw new BusinessException("해당 조직의 데이터가 아닙니다.");
        }
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return "Y".equals(this.delYn);
    }
}