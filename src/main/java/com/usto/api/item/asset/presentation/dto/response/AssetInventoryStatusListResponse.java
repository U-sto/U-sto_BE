package com.usto.api.item.asset.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.usto.api.item.common.model.OperStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
@Schema(description = "물품보유현황 목록 응답")
public class AssetInventoryStatusListResponse {
    @Schema(description = "취득ID")
    private UUID acqId;

    @Schema(description = "물품번호 (분류-식별)", example = "12345678-12345678")
    private String g2bItemNo;

    @Schema(description = "물품명") // G2B 조인
    private String g2bItemNm;

    @Schema(description = "취득일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate acqAt;

    @Schema(description = "취득단가")
    private BigDecimal acqUpr;

    @Schema(description = "정리일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate arrgAt;

    @Schema(description = "운용부서명") // 조직 조인
    private String deptNm;

    @Schema(description = "운용상태")
    private OperStatus operSts;

    @Schema(description = "내용연수")
    private String drbYr;

    @Schema(description = "수량")
    private Integer qty;
}