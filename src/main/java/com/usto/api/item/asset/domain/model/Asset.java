package com.usto.api.item.asset.domain.model;

import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.common.model.OperStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 물품 대장 도메인 모델 (개별 물품)
 * - 팩토리 메서드는 Mapper로 이동
 * - 비즈니스 로직만 유지
 */
@Getter
@Builder
public class Asset {

    private String itmNo;
    private UUID acqId;
    private String g2bDCd;
    private String deptCd;
    private OperStatus operSts;
    private BigDecimal acqUpr;
    private String drbYr;
    private String rmk;
    private String printYn;
    private String orgCd;
    private String delYn;
    private LocalDateTime delAt;
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;

    /**
     * 개별 물품 정보 수정 (취득단가, 내용연수, 비고만)
     */
    public void updateAssetInfo(BigDecimal acqUpr, String drbYr, String rmk) {
        this.acqUpr = acqUpr;
        this.drbYr = drbYr;
        this.rmk = rmk;
    }

    /**
     * 운용부서 배정
     */
    public void assignDepartment(String deptCd) {
        this.deptCd = deptCd;
        this.operSts = OperStatus.OPER;
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return "Y".equals(this.delYn);
    }

    //출력 상태 변경
    public void markAsPrinted(){
        this.printYn = "Y";
    }

    public void updateForOperation(String deptCd) {
        this.deptCd = deptCd;
        this.operSts = OperStatus.OPER;
    }
}