package com.usto.api.item.asset.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
@Schema(description = "물품보유현황 검색 조건")
public class AssetInventoryStatusSearchRequest {
    @Schema(description = "G2B 식별코드")
    private String g2bDCd;

    @Schema(description = "운용부서 코드")
    private String deptCd;

    @Schema(description = "취득일자 시작")
    private LocalDate startAcqAt;

    @Schema(description = "취득일자 종료")
    private LocalDate endAcqAt;

    @Schema(description = "정리일자 시작")
    private LocalDate startApprAt;

    @Schema(description = "정리일자 종료")
    private LocalDate endApprAt;
}