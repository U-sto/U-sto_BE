package com.usto.api.item.acquisition.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.item.acquisition.application.AcquisitionApplication;
import com.usto.api.item.acquisition.presentation.dto.request.AcqApprovalBulkRequest;
import com.usto.api.item.acquisition.presentation.dto.request.AcqRegisterRequest;
import com.usto.api.item.acquisition.presentation.dto.request.AcqRejectBulkRequest;
import com.usto.api.item.acquisition.presentation.dto.request.AcqSearchRequest;
import com.usto.api.item.acquisition.presentation.dto.response.AcqListResponse;
import com.usto.api.item.acquisition.presentation.dto.response.AcqRegisterResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "item-acquisition-controller", description = "물품 취득 관리 API")
@RestController
@RequestMapping("/api/item/acquisitions")
@RequiredArgsConstructor
public class AcquisitionController {

    private final AcquisitionApplication acquisitionApplication;

    // 1. 조회
    @Operation(
            summary = "물품 취득 목록 조회",
            description = "필터 조건(G2B, 날짜, 부서, 상태)에 따라 취득 목록을 조회합니다. 본인 조직의 데이터만 조회됩니다."
    )
    @GetMapping
    public ApiResponse<Page<AcqListResponse>> getList(
            @Valid AcqSearchRequest searchRequest,
            @PageableDefault(size = 30) Pageable pageable, // 기본/최대 30줄 설정
            @AuthenticationPrincipal UserPrincipal principal) {

        Page<AcqListResponse> result = acquisitionApplication.getAcquisitionList(searchRequest, principal.getOrgCd(), pageable);

        return ApiResponse.ok("조회 성공", result);
    }

    // 2. 등록
    @Operation(
            summary = "물품 취득 등록 (MANAGER)",
            description = "새로운 물품 취득 정보를 등록합니다."
    )
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<AcqRegisterResponse> register(
            @Valid @RequestBody AcqRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID acqId = acquisitionApplication.registerAcquisition(
                request, principal.getUsername(), principal.getOrgCd());
        return ApiResponse.ok("취득 등록 성공", new AcqRegisterResponse(acqId));
    }

    // 3. 수정
    @Operation(
            summary = "물품 취득 수정 (MANAGER)",
            description = "작성중(WAIT) 상태인 취득 정보를 수정합니다. 승인 요청 중이거나 확정 및 반려된 데이터는 수정할 수 없습니다."
    )
    @PatchMapping("/{acqId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> update(
            @PathVariable UUID acqId,
            @Valid @RequestBody AcqRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        acquisitionApplication.updateAcquisition(acqId, request, principal.getOrgCd());
        return ApiResponse.ok("수정 성공");
    }

    // 4. 삭제
    @Operation(
            summary = "물품 취득 삭제 (MANAGER)",
            description = "작성중(WAIT) 상태인 취득 정보를 논리 삭제(Soft Delete)합니다."
    )
    @DeleteMapping("/{acqId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> delete(
            @PathVariable UUID acqId,
            @AuthenticationPrincipal UserPrincipal principal) {
        acquisitionApplication.deleteAcquisition(acqId, principal.getOrgCd());
        return ApiResponse.ok("삭제 성공");
    }

    // 5. 승인 요청
    @Operation(
            summary = "물품 취득 승인 요청 (MANAGER)",
            description = "등록된 취득 정보를 관리자(ADMIN)에게 승인 요청(REQUEST)합니다."
    )
    @PostMapping("/{acqId}/request")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> requestApproval(
            @PathVariable UUID acqId,
            @AuthenticationPrincipal UserPrincipal principal) {
        acquisitionApplication.requestApproval(acqId, principal.getOrgCd());
        return ApiResponse.ok("승인 요청 완료");
    }

    // 6. 승인 취소
    @Operation(
            summary = "물품 취득 승인 요청 취소 (MANAGER)",
            description = "승인 요청(REQUEST) 중인 건을 취소하여 삭제시킵니다."
    )
    @PostMapping("/{acqId}/cancel")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> cancelRequest(
            @PathVariable UUID acqId,
            @AuthenticationPrincipal UserPrincipal principal) {
        acquisitionApplication.cancelRequest(acqId, principal.getOrgCd());
        return ApiResponse.ok("승인 요청 취소 완료");
    }

    // 7. 승인 완료
    @Operation(
            summary = "물품 취득 승인 확정 (ADMIN)",
            description = "승인 요청(REQUEST) 건을 승인하여 승인(APPROVAL) 상태로 만듭니다.."
    )
    @PutMapping("/admin/approval")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> approvalRequest(
            @RequestBody @Valid AcqApprovalBulkRequest request, // DTO로 변경
            @AuthenticationPrincipal UserPrincipal principal) {
            acquisitionApplication.approvalAcquisition(
                    request.getAcqIds(),
                    principal.getUsername(),
                    principal.getOrgCd());

        return ApiResponse.ok("취득 승인 확정 성공");
    }

    //요청 반려
    @Operation(
            summary = "취득 요청 반려 (ADMIN)",
            description = "취득 요청(REQUEST) 건을 반려하여 반려(REJECTED) 상태로 만듭니다."
    )
    @PutMapping("/admin/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> approvalReject(
            @RequestBody @Valid AcqRejectBulkRequest request,
            @AuthenticationPrincipal UserPrincipal principal){
        acquisitionApplication.rejectAcquisition(
                request.getAcqIds(),
                principal.getUsername(),
                principal.getOrgCd());
        return ApiResponse.ok("취득 요청 반려 성공");
    }
}