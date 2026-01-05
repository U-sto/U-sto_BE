package com.usto.api.g2b.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.g2b.domain.service.G2BSearchService;
import com.usto.api.g2b.presentation.dto.G2BCategoryResponseDto;
import com.usto.api.g2b.presentation.dto.G2BItemResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @class G2BSearchController
 * @desc G2B 팝업 검색 API 컨트롤러
 */

@RestController
@RequestMapping("/api/g2b")
@RequiredArgsConstructor
public class G2BSearchController {
    private final G2BSearchService g2bSearchService;

    @GetMapping("/categories")
    public ApiResponse<List<G2BCategoryResponseDto>> getCategoryList(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name) {
        List<G2BCategoryResponseDto> result = g2bSearchService.findCategoryList(code, name)
                .stream()
                .map(e -> new G2BCategoryResponseDto(e.getG2bMCd(), e.getG2bMNm()))
                .toList();
        return ApiResponse.ok("분류 조회 성공", result);
    }

    @GetMapping("/items")
    public ApiResponse<List<G2BItemResponseDto>> getItemList(
            @RequestParam String categoryCode,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String itemName) {
        List<G2BItemResponseDto> result = g2bSearchService.findItemList(
                        categoryCode, itemCode, itemName)
                .stream()
                .map(e -> new G2BItemResponseDto(e.getG2bDCd(), e.getG2bDNm(), e.getG2bUpr()))
                .toList();
        return ApiResponse.ok("품목 조회 성공", result);
    }
}