package com.usto.api.item.disposal.presentation.dto.response;

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
@Schema(description = "처분등록목록 응답 (마스터)")
public class DisposalListResponse {

    @Schema(description = "처분ID")
    private UUID dispMId;

    @Schema(description = "처분일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dispAt;

    @Schema(description = "처분정리구분")
    private String dispType;

    @Schema(description = "등록자ID")
    private String aplyUsrId;

    @Schema(description = "등록자명")
    private String aplyUsrNm;

    @Schema(description = "승인상태")
    private String apprSts;

    @Schema(description = "처분 물품 개수")
    private Integer itemCount;
}