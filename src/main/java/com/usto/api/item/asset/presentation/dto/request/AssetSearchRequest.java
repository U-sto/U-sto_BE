package com.usto.api.item.asset.presentation.dto.request;

import com.usto.api.item.common.model.OperStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "운용대장/출력물관리 검색 조건")
public class AssetSearchRequest {

    @Schema(description = "G2B 식별코드")
    private String g2bDCd;

    @Schema(description = "취득일자 시작")
    private LocalDate startAcqAt;

    @Schema(description = "취득일자 종료")
    private LocalDate endAcqAt;

    @Schema(description = "정리일자 시작")
    private LocalDate startArrgAt;

    @Schema(description = "정리일자 종료")
    private LocalDate endArrgAt;

    @Schema(description = "운용부서코드")
    private String deptCd;

    @Schema(description = "운용상태 (ACQ/OPER/RTN/DSU)")
    private OperStatus operSts;

    @Schema(description = "물품고유번호")
    private String itmNo;

    //printYn이 null : 운용대장조회 , 출력물관리 이런 식으로
    @Schema(description = "출력상태 (Y: 출력됨, N: 미출력, null: 전체)", example = "Y", allowableValues = {"Y", "N"})
    private String printYn;  // 선택 파라미터
}