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
    @Operation(
            summary = "물품 취득 목록 조회",
            description = "필터 조건(G2B, 날짜, 부서, 상태)에 따라 취득 목록을 조회합니다. 본인 조직의 데이터만 조회됩니다."
    )
    @GetMapping
    public ApiResponse<List<AcqListResponse>> getList(
            @Valid AcqSearchRequest searchRequest,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("조회 성공", acquisitionService.getAcquisitionList(searchRequest, principal.getOrgCd()));
    }

    // 2. 등록
    @Operation(
            summary = "물품 취득 등록 (ADMIN)",
            description = "새로운 물품 취득 정보를 등록합니다."
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AcqRegisterResponse> register(
            @Valid @RequestBody AcqRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        String acqId = acquisitionService.registerAcquisition(
                request, principal.getUsername(), principal.getOrgCd());
        return ApiResponse.ok("취득 등록 성공", new AcqRegisterResponse(acqId));
    }

    // 3. 수정
    @Operation(
            summary = "물품 취득 수정 (ADMIN)",
            description = "작성중(WAIT) 또는 반려(REJECTED) 상태인 취득 정보를 수정합니다. 승인 요청 중이거나 확정된 데이터는 수정할 수 없습니다."
    )
    @PutMapping("/{acqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> update(
            @PathVariable String acqId,
            @Valid @RequestBody AcqRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        acquisitionService.updateAcquisition(acqId, request, principal.getOrgCd());
        return ApiResponse.ok("수정 성공");
    }

    // 4. 삭제
    @Operation(
            summary = "물품 취득 삭제 (ADMIN)",
            description = "작성중(WAIT) 또는 반려(REJECTED) 상태인 취득 정보를 논리 삭제(Soft Delete)합니다."
    )
    @DeleteMapping("/{acqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable String acqId) {
        acquisitionService.deleteAcquisition(acqId);
        return ApiResponse.ok("삭제 성공");
    }

    // 5. 승인 요청
    @Operation(
            summary = "물품 취득 승인 요청 (ADMIN)",
            description = "등록된 취득 정보를 결재자(MANAGER)에게 승인 요청(REQUEST)합니다."
    )
    @PostMapping("/{acqId}/request")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> requestApproval(@PathVariable String acqId) {
        acquisitionService.requestApproval(acqId);
        return ApiResponse.ok("승인 요청 완료");
    }

    // 6. 승인 취소
    @Operation(
            summary = "물품 취득 승인 요청 취소 (ADMIN)",
            description = "승인 요청(REQUEST) 중인 건을 취소하여 다시 작성중(WAIT) 상태로 되돌립니다."
    )
    @PostMapping("/{acqId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> cancelRequest(@PathVariable String acqId) {
        acquisitionService.cancelRequest(acqId);
        return ApiResponse.ok("승인 요청 취소 완료");
    }
}