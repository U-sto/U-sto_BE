package com.usto.api.item.disposal.presentation.dto.request;

import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.disposal.domain.model.DisposalArrangementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "처분 목록 검색 조건")
public class DisposalSearchRequest {

    @Schema(description = "처분일자 시작")
    private LocalDate startAplyAt;

    @Schema(description = "처분일자 종료")
    private LocalDate endAplyAt;

    @Schema(description = "처분정리구분 (SALE/DONATION/DISCARD/TRANSFER)")
    private DisposalArrangementType dispType;

    @Schema(description = "승인상태 (WAIT/REQUEST/APPROVED/REJECTED)")
    private ApprStatus apprSts;
}