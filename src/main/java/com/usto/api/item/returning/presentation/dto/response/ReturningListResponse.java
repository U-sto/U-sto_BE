package com.usto.api.item.returning.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "반납등록목록 응답 (마스터)")
public class ReturningListResponse {

    @Schema(description = "반납ID")
    private UUID rtrnMId;

    @Schema(description = "반납일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate aplyAt;

    @Schema(description = "반납확정일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate rtrnApprAt;

    @Schema(description = "등록자ID")
    private String aplyUsrId;

    @Schema(description = "등록자명")
    private String aplyUsrNm;

    @Schema(description = "승인상태")
    private String apprSts;

    @Schema(description = "반납 물품 개수")
    private Integer itemCount;
}