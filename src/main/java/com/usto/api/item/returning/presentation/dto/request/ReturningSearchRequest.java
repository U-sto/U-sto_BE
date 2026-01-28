package com.usto.api.item.returning.presentation.dto.request;

import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "반납 목록 검색 조건")
public class ReturningSearchRequest {

    @Schema(description = "반납일자 시작")
    private LocalDate startAplyAt;

    @Schema(description = "반납일자 종료")
    private LocalDate endAplyAt;

    @Schema(description = "승인상태")
    private ApprStatus apprSts;
}