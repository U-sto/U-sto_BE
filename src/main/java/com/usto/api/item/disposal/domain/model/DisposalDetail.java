package com.usto.api.item.disposal.domain.model;

import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.disuse.domain.model.DisuseReason;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 처분 상세 도메인 모델
 * - 불용 테이블에서 ITEM_STS, CHG_RSN 값을 가져와 저장
 */
@Getter
@Builder
public class DisposalDetail {

    private UUID dispDId;
    private UUID dispMId;
    private String itmNo;
    private ItemStatus itemSts;     // 불용기본 테이블의 물품상태
    private DisuseReason chgRsn;    // 불용기본 테이블의 사유
    private String orgCd;
    private String delYn;
    private LocalDateTime delAt;
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;
}