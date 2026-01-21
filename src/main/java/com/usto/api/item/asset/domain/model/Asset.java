package com.usto.api.item.asset.domain.model;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.common.model.OperStatus;
import lombok.Builder;
import lombok.Getter;

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
     * 신규 물품 대장기본/상세 생성 (물품번호 생성기를 사용)
     */
    // TODO: 물품취득 승인 시 이용할 메서드 구현 - 대장기본을 한개 생성하고,
    //  물품번호생성기를 사용해서 대장상세를 취득수량만큼 만들고 필요한 스냅샷 등을 취득 건으로부터 채워넣음
    public static Asset create() { return null; }


    /**
     * 물품 정보 수정 (취득단가, 내용연수, 비고)
     */
    public void updateInfo(BigDecimal acqUpr, String drbYr, String rmk) {
        this.acqUpr = acqUpr;
        this.drbYr = drbYr;
        this.rmk = rmk;
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