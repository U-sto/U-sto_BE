package com.usto.api.item.asset.domain.model;

import com.usto.api.item.common.model.OperStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 물품 상태 이력 도메인 모델
 */
@Getter
@Builder
public class AssetStatusHistory {

    private UUID itemHisId;              // 상태이력ID (PK)
    private String itmNo;                // 물품고유번호
    private OperStatus prevSts;          // 이전상태
    private OperStatus newSts;           // 변경상태
    private String chgRsn;               // 변경사유
    private String reqUsrId;             // 등록자ID
    private LocalDate reqAt;             // 등록일자
    private String apprUsrId;            // 확정자ID
    private LocalDate apprAt;            // 확정일자(변경일자)
    private String orgCd;                // 조직코드
    private String delYn;
    private LocalDateTime delAt;
    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;

    /**
     * 상태 이력 생성 팩토리 메서드
     */
    public static AssetStatusHistory create(
            String itmNo,
            OperStatus prevSts,
            OperStatus newSts,
            String chgRsn,
            String reqUsrId,
            LocalDate reqAt,
            String apprUsrId,
            LocalDate apprAt,
            String orgCd
    ) {
        return AssetStatusHistory.builder()
                .itemHisId(UUID.randomUUID())
                .itmNo(itmNo)
                .prevSts(prevSts)
                .newSts(newSts)
                .chgRsn(chgRsn)
                .reqUsrId(reqUsrId)
                .reqAt(reqAt)
                .apprUsrId(apprUsrId)
                .apprAt(apprAt)
                .orgCd(orgCd)
                .delYn("N")
                .build();
    }
}