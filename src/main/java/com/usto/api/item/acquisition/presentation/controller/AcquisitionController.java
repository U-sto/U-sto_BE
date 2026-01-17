package com.usto.api.item.acquisition.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.item.acquisition.application.AcquisitionService;
import com.usto.api.item.acquisition.presentation.dto.request.AcqRegisterRequest;
import com.usto.api.item.acquisition.presentation.dto.request.AcqSearchRequest;
import com.usto.api.item.acquisition.presentation.dto.response.AcqListResponse;
import com.usto.api.item.acquisition.presentation.dto.response.AcqRegisterResponse;
import com.usto.api.user.domain.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "item-acquisition-controller", description = "물품 취득 관리 API")
@RestController
@RequestMapping("/api/item/acquisitions")
@RequiredArgsConstructor
public class AcquisitionController {

    private final AcquisitionService acquisitionService;

    @Operation(summary = "물품 취득 등록", description = "새로운 물품 취득 정보를 등록합니다.")
    @PostMapping
    public ApiResponse<AcqRegisterResponse> register(
            @Valid @RequestBody AcqRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = principal.getUsername();
        String orgCd = principal.getOrgCd();

        Long acqId = acquisitionService.registerAcquisition(request, userId, orgCd);
        return ApiResponse.ok("취득 등록 성공", new AcqRegisterResponse(acqId));
    }

    @Operation(summary = "물품 취득 목록 조회", description = "필터 조건에 따라 취득 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<AcqListResponse>> getList(
            @Valid AcqSearchRequest searchRequest,
            @AuthenticationPrincipal UserPrincipal principal) {

        List<AcqListResponse> list = acquisitionService.getAcquisitionList(searchRequest, principal.getOrgCd());

        if (list.isEmpty()) {
            return ApiResponse.ok("조회 결과가 없습니다.", list);
        }
        return ApiResponse.ok("취득 목록 조회 성공", list);
    }
}