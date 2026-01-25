package com.usto.api.item.asset.presentation.controller;

import com.usto.api.item.asset.application.AssetService;
import com.usto.api.item.asset.presentation.dto.response.AssetAiItemDetailResponse;
import com.usto.api.user.domain.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "ai-controller", description = "AI를 위한 API")
@RestController
@RequestMapping("/api/ai/item/assets")
@RequiredArgsConstructor
public class AssetSearchForAiController {

    private final AssetService assetService;

    @Operation(
            summary = "물품 조회 By AI",
            description = "물품의 G2B목록명,G2B목록번호,물품고유번호 중에 하나라도 들어오면 전체 정보를 준다."
    )
    @GetMapping
    public ApiResponseForAi<AssetAiItemDetailResponse> getAssetList(
            @Parameter(description = "물품고유번호", example = "M202600001",required = false)
            @RequestParam(required = false)
            String itmNo,
            @Parameter(description = "G2B분류번호", example = "12345678",required = false)
            @RequestParam(required = false)
            String g2bMCd,
            @Parameter(description = "G2B식별번호", example = "12345678",required = false)
            @RequestParam(required = false)
            String g2bDCd,
            @Parameter(description = "G2B목록명", example = "12345678",required = false)
            @RequestParam(required = false)
            String g2bDNm,
            @AuthenticationPrincipal UserPrincipal principal) {

        if(itmNo == null && g2bMCd == null && g2bDCd == null && g2bDNm == null){
            return ApiResponseForAi.fail("조회 실패");
        }

        List<AssetAiItemDetailResponse> result =
                assetService.getAssetList(itmNo, g2bMCd, g2bDCd, g2bDNm, principal.getOrgCd());
        return ApiResponseForAi.ok("조회 성공",result);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ApiResponseForAi<T> {

        private final boolean success;
        private final String message;
        private final List<T> datas;

        public static <T> ApiResponseForAi<T> ok(String message, List<T> datas) {
            return new ApiResponseForAi<>(true, message, datas);
        }


        public static <T> ApiResponseForAi<T> fail(String message) {
            return new ApiResponseForAi<>(false, message, List.of());
        }
    }
}
