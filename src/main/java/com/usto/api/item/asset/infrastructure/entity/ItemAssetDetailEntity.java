package com.usto.api.item.asset.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import com.usto.api.item.common.model.OperStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TB_ITEM002D - 물품대장상세 (개별 물품 단위)
 */
@Entity
@Table(name = "TB_ITEM002D")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE TB_ITEM002D SET del_yn = 'Y', del_at = NOW() WHERE itm_no = ?")
@Where(clause = "DEL_YN = 'N'")
public class ItemAssetDetailEntity extends BaseTimeEntity {

    @Id
    @Column(name = "ITM_NO", length = 10, columnDefinition = "char(10)")
    private String itmNo;  // 물품고유번호 (M + 연도4자리 + 순번5자리)

    @Column(name = "ACQ_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID acqId;    // 취득ID

    @Column(name = "G2B_D_CD", nullable = false, length = 8, columnDefinition = "char(8)")
    private String g2bDCd;

    @Column(name = "DEPT_CD", nullable = false, length = 5, columnDefinition = "char(5)")
    private String deptCd;

    @Enumerated(EnumType.STRING)
    @Column(name = "OPER_STS", nullable = false, length = 30)
    private OperStatus operSts; // ACQ/OPER/RTN/DSU

    @Column(name = "ACQ_UPR", nullable = false, precision = 20)
    private BigDecimal acqUpr;  // 수정 가능

    @Column(name = "DRB_YR", nullable = false, length = 20)
    private String drbYr;       // 수정 가능

    @Column(name = "RMK", length = 500)
    private String rmk;         // 수정 가능

    @Builder.Default
    @Column(name = "PRINT_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String printYn = "N";

    @Column(name = "ORG_CD", nullable = false, length = 7, columnDefinition = "char(7)")
    private String orgCd;

    @Builder.Default
    @Column(name = "DEL_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String delYn = "N";

    @Column(name = "DEL_AT")
    private LocalDateTime delAt;

    /**
     * 개별 물품 정보 수정 (취득단가, 내용연수, 비고만)
     */
    public void updateAssetInfo(BigDecimal acqUpr, String drbYr, String rmk) {
        this.acqUpr = acqUpr;
        this.drbYr = drbYr;
        this.rmk = rmk;
    }

    /**
     * 운용부서 변경 (반납 상태일 때만 가능)
     */
    public void assignDepartment(String deptCd) {
        if (this.operSts != OperStatus.RTN) {
            throw new IllegalStateException("반납 상태일 때만 부서 배정이 가능합니다.");
        }
        this.deptCd = deptCd;
        this.operSts = OperStatus.OPER;  // 운용 상태로 변경
    }

    /**
     * 반납 처리 (부서코드 제거)
     */
    public void returnAsset() {
        this.deptCd = null;
        this.operSts = OperStatus.RTN;
    }
}