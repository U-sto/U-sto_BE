package com.usto.api.item.returning.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 반납 상세 도메인 모델 (TB_ITEM003D)
 */
@Getter
@Builder
public class ReturningDetail {

    private UUID rtrnDId;       // 반납상세ID
    private UUID rtrnMId;       // 반납ID
    private String itmNo;       // 물품고유번호
    private String deptCd;      // 운용부서코드 (스냅샷)
    private String orgCd;       // 조직코드
    private String delYn;
    private LocalDateTime delAt;
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;
}