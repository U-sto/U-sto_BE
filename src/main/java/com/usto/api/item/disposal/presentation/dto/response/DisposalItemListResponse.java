package com.usto.api.item.disposal.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.disposal.domain.model.DisposalArrangementType;
import com.usto.api.item.disuse.domain.model.DisuseReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "처분물품목록 응답 (상세)")
public class DisposalItemListResponse {

    @Schema(description = "G2B 목록번호 (분류-식별)", example = "12345678-12345678")
    private String g2bItemNo;

    @Schema(description = "G2B 목록명")
    private String g2bDNm;

    @Schema(description = "물품고유번호", example = "M202600001")
    private String itmNo;

    @Schema(description = "취득일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate acqAt;

    @Schema(description = "취득금액")
    private BigDecimal acqUpr;

    @Schema(description = "처분정리구분")
    private DisposalArrangementType dispType;

    @Schema(description = "물품상태 (불용 신청 당시)")
    private ItemStatus itemSts;

    @Schema(description = "불용사유 (불용 신청 당시)")
    private DisuseReason chgRsn;
}