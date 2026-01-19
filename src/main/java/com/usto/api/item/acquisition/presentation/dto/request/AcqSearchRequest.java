package com.usto.api.item.acquisition.presentation.dto.request;

import com.usto.api.item.common.model.ApprStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class AcqSearchRequest {
    private String g2bDCd;        // 필터: G2B 식별코드
    private String deptCd;        // 필터: 운용부서
    private LocalDate startAcqAt; // 필터: 취득일자 시작
    private LocalDate endAcqAt;   // 필터: 취득일자 종료
    private LocalDate startApprAt;// 필터: 정리일자 시작
    private LocalDate endApprAt;  // 필터: 정리일자 종료
    private ApprStatus apprSts;   // 필터: 승인상태 (전체일 경우 null)
}