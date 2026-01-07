package com.usto.api.g2b.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.g2b.domain.service.G2bSearchService;
import com.usto.api.g2b.presentation.dto.G2bCategoryResponseDto;
import com.usto.api.g2b.presentation.dto.G2bItemResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @class G2bSearchController
 * @desc G2B 팝업 검색 API 컨트롤러
 */

@Tag(name = "g2b-search-controller", description = "G2B 검색 관련 API")
@RestController
@RequestMapping("/api/g2b")
@RequiredArgsConstructor
public class G2bSearchController {
    private final G2bSearchService g2bSearchService;

    @Operation(
            summary = "G2B 물품 분류 조회",
            description = "물품분류코드와 분류명으로 G2B 물품 분류를 조회합니다. " +
                    "검색어 미입력 시 전체 목록을 반환합니다."
    )
    @GetMapping("/categories")
    public ApiResponse<List<G2bCategoryResponseDto>> getCategoryList(
            @Parameter(description = "물품분류코드")
            @RequestParam(required = false) String code,
            @Parameter(description = "물품분류명")
            @RequestParam(required = false) String name) {
        List<G2bCategoryResponseDto> categories = g2bSearchService.findCategoryList(code, name)
                .stream()
                .map(e -> new G2bCategoryResponseDto(e.getG2bMCd(), e.getG2bMNm()))
                .toList();
        if (categories.isEmpty()) {
            return ApiResponse.ok("조회 결과가 없습니다.", categories);
        }
        return ApiResponse.ok("분류 조회 성공", categories);
    }

    @Operation(
            summary = "G2B 물품 품목 조회",
            description = "물품분류코드, 물품식별코드와 품목명으로 G2B 물품 품목을 조회합니다. " +
                    "검색어 미입력 시 빈 리스트를 반환합니다."
    )
    @GetMapping("/items")
    public ApiResponse<List<G2bItemResponseDto>> getItemList(
            @Parameter(description = "물품분류코드")
            @RequestParam(required = false) String categoryCode,
            @Parameter(description = "물품식별코드")
            @RequestParam(required = false) String itemCode,
            @Parameter(description = "물품품목명")
            @RequestParam(required = false) String itemName) {
        List<G2bItemResponseDto> items = g2bSearchService.findItemList(
                        categoryCode, itemCode, itemName)
                .stream()
                .map(e -> new G2bItemResponseDto(e.getG2bDCd(), e.getG2bDNm(), e.getG2bUpr()))
                .toList();
        if (items.isEmpty()) {
            return ApiResponse.ok("조회 결과가 없습니다.", items);
        }
        return ApiResponse.ok("품목 조회 성공", items);
    }
}