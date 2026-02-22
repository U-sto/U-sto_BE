package com.usto.api.item.acquisition.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
@Schema(description = "물품 취득 목록 응답")
public class AcqListResponse {
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

    @Schema(description = "확정(정리)일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate apprAt;

    @Schema(description = "운용부서명") // 조직 조인
    private String deptNm;

    @Schema(description = "운용상태 (항상 null 값)")
    private String operSts;

    @Schema(description = "내용연수")
    private String drbYr;

    @Schema(description = "수량")
    private Integer acqQty;

    @Schema(description = "승인상태")
    private String apprSts;
}