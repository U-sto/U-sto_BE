package com.usto.api.item.asset.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.item.asset.application.AssetApplication;
import com.usto.api.item.asset.presentation.dto.response.AssetListResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetPublicDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "public-asset-controller", description = "물품 공개 조회 API (인증 불필요)")
@Controller
@RequestMapping("/api/public/item")
@RequiredArgsConstructor
public class PublicAssetController {

    private final AssetApplication assetApplication;

    @Operation(
            summary = "물품 정보 조회 (QR 스캔용)",
            description = "물품고유번호로 최신 정보를 조회합니다. 인증이 필요 없습니다."
    )
    @GetMapping("/{orgCd}/{itmNo}")
    public String  getItemByQR(
            @Parameter(description = "조직코드")
            @PathVariable String orgCd,
            @Parameter(description = "물품고유번호", example = "M202600001")
            @PathVariable String itmNo,
            Model model
            ) {

        AssetPublicDetailResponse item = assetApplication.getAssetPublicDetail(orgCd,itmNo);

        model.addAttribute("item", item);

        return "item-detail";
    }
}
