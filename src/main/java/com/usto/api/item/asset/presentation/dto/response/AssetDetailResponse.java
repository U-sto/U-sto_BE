package com.usto.api.item.asset.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "개별 물품 상세 정보 응답")
public class AssetDetailResponse {

    @Schema(description = "물품고유번호", example = "M202600001")
    private String itmNo;

    @Schema(description = "G2B 목록명")
    private String g2bDNm;

    @Schema(description = "G2B 목록번호 (분류-식별)", example = "12345678-12345678")
    private String g2bItemNo;

    @Schema(description = "취득일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate acqAt;

    @Schema(description = "정리일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate arrgAt;

    @Schema(description = "운용상태")
    private String operSts;

    @Schema(description = "내용연수")
    private String drbYr;

    @Schema(description = "취득금액")
    private BigDecimal acqUpr;

    @Schema(description = "수량")
    private Integer qty;

    @Schema(description = "취득정리구분")
    private String acqArrgTy;

    @Schema(description = "운용부서명")
    private String deptNm;

    @Schema(description = "운용부서코드")
    private String deptCd;

    @Schema(description = "비고")
    private String rmk;

    @Schema(description = "물품상태이력 목록")
    private List<StatusHistoryDto> statusHistories;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "물품 상태 이력")
    public static class StatusHistoryDto {

        @Schema(description = "상태이력ID")
        private String itemHisId;

        @Schema(description = "물품고유번호")
        private String itmNo;

        @Schema(description = "이전상태")
        private String prevSts;

        @Schema(description = "변경상태")
        private String newSts;

        @Schema(description = "변경사유")
        private String chgRsn;

        @Schema(description = "등록자ID")
        private String reqUsrId;

        @Schema(description = "등록일자")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate reqAt;

        @Schema(description = "확정자ID")
        private String apprUsrId;

        @Schema(description = "확정일자(변경일자)")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate apprAt;
    }
}