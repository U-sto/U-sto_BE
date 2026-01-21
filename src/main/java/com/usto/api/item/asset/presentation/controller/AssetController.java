package com.usto.api.item.asset.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.item.asset.application.AssetService;
import com.usto.api.item.asset.presentation.dto.request.AssetSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetListResponse;
import com.usto.api.user.domain.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
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
}