package com.usto.api.item.asset.domain.model;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.common.model.OperStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 물품 대장 도메인 모델 (개별 물품)
 */
@Getter
@Builder
public class Asset {

    private String itmNo;       // 물품고유번호
    private UUID acqId;         // 취득ID
    private String g2bDCd;      // G2B 식별코드
    private String deptCd;      // 운용부서코드
    private OperStatus operSts; // 운용상태
    private BigDecimal acqUpr;  // 취득단가
    private String drbYr;       // 내용연수
    private String rmk;         // 비고
    private String printYn;     // 출력여부
    private String orgCd;       // 조직코드
    private String delYn;
    private LocalDateTime delAt;
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;


    // ===== 비즈니스 로직 메서드 =====

    /**
     * 신규 물품대장 디테일 생성 팩토리 메서드
     */
    public static Asset create(String itmNo, UUID acqId, String g2bDCd,
                                              String deptCd, OperStatus operStatus,
                                              BigDecimal acqUpr, String drbYr, String orgCd) {
        return Asset.builder()
                .itmNo(itmNo)
                .acqId(acqId)
                .g2bDCd(g2bDCd)
                .deptCd(deptCd)
                .operSts(operStatus)
                .acqUpr(acqUpr)
                .drbYr(drbYr)
                .orgCd(orgCd)
                .printYn("N")
                .delYn("N")
                .build();
    }

    /**
     * 개별 물품 정보 수정 (취득단가, 내용연수, 비고만)
     */
    public void updateAssetInfo(BigDecimal acqUpr, String drbYr, String rmk) {
        // 1. 상태 검증: 삭제되었거나 불용(DSU) 상태인 물품은 수정 불가
        validateActiveStatus();

        // 2. 값 검증: 취득단가는 0원 이상이어야 함
        if (acqUpr == null || acqUpr.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("취득 단가는 0원 이상이어야 합니다.");
        }

        this.acqUpr = acqUpr;
        this.drbYr = drbYr;
        this.rmk = rmk;
    }

    /**
     * 반납 처리 (부서코드 제거)
     */
    public void returnAsset() {
        validateActiveStatus(); // 삭제/불용 체크

        if (!StringUtils.hasText(deptCd) || "NONE".equals(deptCd)) {
            throw new BusinessException("배정할 부서 코드가 유효하지 않습니다.");
        }

        this.deptCd = "NONE";    // 반납 시 부서 공란
        this.operSts = OperStatus.RTN;
    }

    /**
     * 운용부서 배정 (반납 상태일 때만 가능)
     */
    public void assignDepartment(String deptCd) {
        if (this.operSts != OperStatus.RTN) {
            throw new BusinessException("반납 상태일 때만 부서 배정이 가능합니다.");
        }
        this.deptCd = deptCd;
        this.operSts = OperStatus.OPER;
    }

    /**
     * 공통 검증: 물품이 수정 가능한 활성 상태인지 확인
     */
    private void validateActiveStatus() {
        if (isDeleted()) {
            throw new BusinessException("삭제된 물품은 수정할 수 없습니다.");
        }
        if (this.operSts == OperStatus.DSU) {
            throw new BusinessException("불용(DSU) 처리된 물품은 수정할 수 없습니다.");
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

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return "Y".equals(this.delYn);
    }
}