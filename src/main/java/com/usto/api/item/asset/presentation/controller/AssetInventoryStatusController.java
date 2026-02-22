package com.usto.api.item.asset.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.item.asset.application.AssetApplication;
import com.usto.api.item.asset.presentation.dto.request.AssetInventoryStatusSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetInventoryStatusDetailResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetInventoryStatusListResponse;
import com.usto.api.item.common.model.OperStatus;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "item-asset-inventory-status-controller", description = "물품 보유 현황 조회 API")
@RestController
@RequestMapping("/api/item/asset-inventory-status")
@RequiredArgsConstructor
public class AssetInventoryStatusController {

    private final AssetApplication assetApplication;

    @Operation(
            summary = "물품 보유현황 목록 조회",
            description = "승인된 취득 건의 현재 보유 현황을 조회합니다. 같은 속성(운용부서, 운용상태, 취득금액, 내용연수, 비고)을 가진 물품들끼리 그룹핑하여 수량으로 표시합니다."
    )
    @GetMapping
    public ApiResponse<Page<AssetInventoryStatusListResponse>> getInventoryStatusList(
            @Valid AssetInventoryStatusSearchRequest searchRequest,
            @PageableDefault(size = 30) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ApiResponse.ok("조회 성공",
                assetApplication.getInventoryStatusList(searchRequest, principal.getOrgCd(), pageable));
    }

    @Operation(
            summary = "물품 보유현황 상세 조회",
            description = "보유현황 목록에서 선택한 그룹의 상세 정보를 조회합니다. 해당 그룹에 속한 모든 물품고유번호 목록을 포함합니다."
    )
    @GetMapping("/detail")
    public ApiResponse<AssetInventoryStatusDetailResponse> getInventoryStatusDetail(
            @Parameter(description = "취득ID") @RequestParam UUID acqId,
            @Parameter(description = "운용부서코드") @RequestParam String deptCd,
            @Parameter(description = "운용상태",
                    schema = @Schema(type = "string", allowableValues = {"OPER", "RTN", "DSU"}, example = "OPER")
            ) @RequestParam OperStatus operSts,
            @Parameter(description = "취득금액") @RequestParam BigDecimal acqUpr,
            @Parameter(description = "내용연수") @RequestParam String drbYr,
            @Parameter(description = "비고") @RequestParam(required = false) String rmk,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ApiResponse.ok("조회 성공",
                assetApplication.getInventoryStatusDetail(
                        acqId, deptCd, operSts, acqUpr, drbYr, rmk, principal.getOrgCd()));
    }
}