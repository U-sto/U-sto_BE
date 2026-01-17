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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "item-acquisition-controller", description = "물품 취득 관리 API")
@RestController
@RequestMapping("/api/item/acquisitions")
@RequiredArgsConstructor
public class AcquisitionController {

    private final AcquisitionService acquisitionService;

    // 1. 조회
    @GetMapping
    public ApiResponse<List<AcqListResponse>> getList(
            @Valid AcqSearchRequest searchRequest,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("조회 성공", acquisitionService.getAcquisitionList(searchRequest, principal.getOrgCd()));
    }

    // 2. 등록
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AcqRegisterResponse> register(
            @Valid @RequestBody AcqRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long acqId = acquisitionService.registerAcquisition(request, principal.getUsername(), principal.getOrgCd());
        return ApiResponse.ok("취득 등록 성공", new AcqRegisterResponse(acqId));
    }

    // 3. 수정
    @PutMapping("/{acqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> update(
            @PathVariable Long acqId,
            @Valid @RequestBody AcqRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        acquisitionService.updateAcquisition(acqId, request, principal.getOrgCd());
        return ApiResponse.ok("수정 성공");
    }

    // 4. 삭제
    @DeleteMapping("/{acqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long acqId) {
        acquisitionService.deleteAcquisition(acqId);
        return ApiResponse.ok("삭제 성공");
    }

    // 5. 승인 요청
    @PostMapping("/{acqId}/request")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> requestApproval(@PathVariable Long acqId) {
        acquisitionService.requestApproval(acqId);
        return ApiResponse.ok("승인 요청 완료");
    }

    // 6. 승인 취소
    @PostMapping("/{acqId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> cancelRequest(@PathVariable Long acqId) {
        acquisitionService.cancelRequest(acqId);
        return ApiResponse.ok("승인 요청 취소 완료");
    }
}