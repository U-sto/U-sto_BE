package com.usto.api.item.asset.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.item.asset.application.AssetService;
import com.usto.api.item.asset.presentation.dto.request.AssetSearchRequest;
import com.usto.api.item.asset.presentation.dto.request.AssetUpdateRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetDetailResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetListResponse;
import com.usto.api.user.domain.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "item-asset-controller", description = "물품 운용 관리 API")
@RestController
@RequestMapping("/api/item/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @Operation(
            summary = "운용대장 목록 조회",
            description = "필터 조건(G2B, 취득일자, 정리일자, 부서, 운용상태, 물품번호)에 따라 운용대장을 조회합니다. 논리삭제된 물품은 제외됩니다."
    )
    @GetMapping
    public ApiResponse<List<AssetListResponse>> getList(
            @Valid AssetSearchRequest searchRequest,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("조회 성공",
                assetService.getAssetList(searchRequest, principal.getOrgCd()));
    }

    @Operation(
            summary = "개별 물품 상세 조회",
            description = "물품고유번호로 개별 물품의 상세 정보와 상태 이력을 조회합니다."
    )
    @GetMapping("/{itmNo}")
    public ApiResponse<AssetDetailResponse> getDetail(
            @Parameter(description = "물품고유번호", example = "M202600001")
            @PathVariable String itmNo,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("조회 성공",
                assetService.getAssetDetail(itmNo, principal.getOrgCd()));
    }

    @Operation(
            summary = "개별 물품 정보 수정",
            description = "취득단가, 내용연수, 비고를 수정합니다. 삭제되었거나 불용(DSU) 상태인 물품은 수정할 수 없습니다."
    )
    @PutMapping("/{itmNo}")
    public ApiResponse<Void> updateAsset(
            @Parameter(description = "물품고유번호", example = "M202600001")
            @PathVariable String itmNo,
            @Valid @RequestBody AssetUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        assetService.updateAssetInfo(itmNo, request, principal.getOrgCd());
        return ApiResponse.ok("수정 성공");
    }
}