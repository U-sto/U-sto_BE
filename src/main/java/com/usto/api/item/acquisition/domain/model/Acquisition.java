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

/**
 * 취득 도메인 모델
 * - 팩토리 메서드 제거 (Mapper로 이동)
 * - 비즈니스 로직만 유지
 */
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
     * 취득 정보 수정
     */
    public void updateInfo(
            String g2bDCd,
            LocalDate acqAt,
            BigDecimal acqUpr,
            String deptCd,
            String drbYr,
            Integer acqQty,
            AcqArrangementType arrgTy,
            String rmk
    ) {
        this.g2bDCd = g2bDCd;
        this.acqAt = acqAt;
        this.acqUpr = acqUpr;
        this.deptCd = deptCd;
        this.drbYr = drbYr;
        this.acqQty = acqQty;
        this.arrgTy = arrgTy;
        this.rmk = rmk;
    }

    /**
     * 승인 요청
     */
    public void requestApproval() {
        this.apprSts = ApprStatus.REQUEST;
    }

    /**
     * 승인 확정
     */
    public void confirmApproval(String userId) {
        this.apprUsrId = userId;
        this.apprSts = ApprStatus.APPROVED;
        this.apprAt = LocalDate.now(KOREA_ZONE);
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return "Y".equals(this.delYn);
    }
}