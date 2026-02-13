package com.usto.api.item.operation.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 운용 상세 도메인 모델 (TB_ITEM003D)
 */
@Getter
@Builder
public class OperationDetail {

    private UUID operDId;       // 운용상세ID
    private UUID operMId;       // 운용ID
    private String itmNo;       // 물품고유번호
    private String orgCd;       // 조직코드
    private String delYn;
    private LocalDateTime delAt;
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;
}