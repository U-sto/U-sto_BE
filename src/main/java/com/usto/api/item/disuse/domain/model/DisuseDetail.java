package com.usto.api.item.disuse.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 불용 상세 도메인 모델
 */
@Getter
@Builder
public class DisuseDetail {

    private UUID dsuDId;
    private UUID dsuMId;
    private String itmNo;
    private String deptCd;
    private String orgCd;
    private String delYn;
    private LocalDateTime delAt;
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;
}