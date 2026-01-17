package com.usto.api.item.acquisition.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import com.usto.api.item.acquisition.domain.model.AcqArrangementType;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.OperStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ITEM001M")
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE tb_item001m SET del_yn = 'Y', del_at = NOW() WHERE acq_id = ?")
@Where(clause = "del_yn = 'N'") // 조회 시 삭제 안 된 데이터만 기본으로 가져옴
public class ItemAcquisitionEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACQ_ID")
    private Long acqId;           // 취득 ID

    @Column(name = "G2B_D_CD", nullable = false, length = 8, columnDefinition = "char(8)")
    private String g2bDCd;        // G2B 식별코드

    @Column(name = "ACQ_AT", nullable = false)
    private LocalDate acqAt;      // 취득일자

    @Column(name = "ACQ_UPR", nullable = false, precision = 20)
    private BigDecimal acqUpr;    // 취득금액

    @Column(name = "DEPT_CD", nullable = false, length = 50)
    private String deptCd;        // 운용부서코드

    @Enumerated(EnumType.STRING)
    @Column(name = "OPER_STS", nullable = false, length = 30)
    private OperStatus operSts;   // 운용상태

    @Column(name = "DRB_YR", nullable = false, length = 20)
    private String drbYr;         // 내용연수

    @Column(name = "ACQ_QTY", nullable = false)
    private Integer acqQty;       // 취득수량

    @Enumerated(EnumType.STRING)
    @Column(name = "ARRG_TY", nullable = false, length = 20)
    private AcqArrangementType arrgTy;  // 정리구분

    @Enumerated(EnumType.STRING)
    @Column(name = "APPR_STS", nullable = false, length = 20)
    private ApprStatus apprSts;   // 승인상태

    @Column(name = "RMK", length = 500)
    private String rmk;           // 비고

    @Column(name = "APLY_USR_ID", nullable = false, length = 30)
    private String aplyUsrId;     // 등록자ID

    @Column(name = "APPR_USR_ID", length = 30)
    private String apprUsrId;     // 확정자ID

    @Column(name = "APPR_AT")
    private LocalDate apprAt;     // 확정일자 (=정리일자)

    @Column(name = "ORG_CD", nullable = false, length = 50)
    private String orgCd;         // 조직코드

    @Builder.Default
    @Column(name = "DEL_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String delYn = "N";   // 삭제여부

    @Column(name = "DEL_AT")
    private LocalDateTime delAt;  // 삭제일시
}