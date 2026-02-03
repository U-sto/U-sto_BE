package com.usto.api.item.disuse.presentation.dto.request;

import com.usto.api.item.common.model.ApprStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "불용 목록 검색 조건")
public class DisuseSearchRequest {

    @Schema(description = "불용일자 시작")
    private LocalDate startAplyAt;

    @Schema(description = "불용일자 종료")
    private LocalDate endAplyAt;

    @Schema(description = "승인상태")
    private ApprStatus apprSts;
}