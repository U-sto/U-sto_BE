package com.usto.api.item.asset.domain.model;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.acquisition.domain.model.AcqArrangementType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * 물품 대장 기본 도메인 모델 (Header)
 */
@Getter
@Builder
public class AssetMaster {
    private final UUID acqId;                    // 취득ID (PK)
    private final String g2bDCd;                 // G2B 식별코드
    private Integer qty;                         // 총 수량 (가변)
    private final LocalDate acqAt;               // 취득일자
    private LocalDate arrgAt;                    // 정리일자
    private final AcqArrangementType acqArrgTy;  // 취득정리구분
    private final String orgCd;                  // 조직코드
    private String delYn;
    private LocalDateTime delAt;

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    /**
     * 수량 감소 로직 (일부 물품 처분 시)
     */
    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new BusinessException("감소 수량은 0보다 커야 합니다.");
        }
        if (this.qty < amount) {
            throw new BusinessException("현재 수량보다 많은 양을 처분할 수 없습니다.");
        }
        this.qty -= amount;
    }
}