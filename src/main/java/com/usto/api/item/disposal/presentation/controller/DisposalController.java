package com.usto.api.item.disposal.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.item.disposal.application.DisposalApplication;
import com.usto.api.item.disposal.presentation.dto.request.DisposalRegisterRequest;
import com.usto.api.item.disposal.presentation.dto.request.DisposalSearchRequest;
import com.usto.api.item.disposal.presentation.dto.response.DisposalItemListResponse;
import com.usto.api.item.disposal.presentation.dto.response.DisposalListResponse;
import com.usto.api.item.disposal.presentation.dto.response.DisposalRegisterResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "item-disposal-controller", description = "물품 처분 관리 API")
@RestController
@RequestMapping("/api/item/disposals")
@RequiredArgsConstructor
public class DisposalController {

    private final DisposalApplication disposalApplication;

    @Operation(
            summary = "처분등록목록 조회 (페이징)",
            description = "처분 신청 마스터 목록을 페이징하여 조회합니다. 기본 30개씩 조회됩니다."
    )
    @GetMapping
    public ApiResponse<Page<DisposalListResponse>> getList(
            @Valid DisposalSearchRequest searchRequest,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "30")
            @RequestParam(defaultValue = "30") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("creAt").ascending());

        return ApiResponse.ok("조회 성공",
                disposalApplication.getDisposalList(searchRequest, principal.getOrgCd(), pageable));
    }

    @Operation(
            summary = "처분물품목록 조회 (페이징)",
            description = "특정 처분 신청의 상세 물품 목록을 페이징하여 조회합니다."
    )
    @GetMapping("/{dispMId}/items")
    public ApiResponse<Page<DisposalItemListResponse>> getItems(
            @PathVariable UUID dispMId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "30")
            @RequestParam(defaultValue = "30") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("itmNo").ascending());

        return ApiResponse.ok("조회 성공",
                disposalApplication.getDisposalItems(dispMId, principal.getOrgCd(), pageable));
    }

    @Operation(
            summary = "처분 신청 등록 (MANAGER)",
            description = "불용(DSU) 상태의 물품을 처분 신청합니다. 여러 물품을 한 번에 신청할 수 있습니다."
    )
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<DisposalRegisterResponse> register(
            @Valid @RequestBody DisposalRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID dispMId = disposalApplication.registerDisposal(
                request,
                principal.getUsername(),
                principal.getOrgCd()
        );
        return ApiResponse.ok("처분 신청 등록 성공", new DisposalRegisterResponse(dispMId));
    }

    @Operation(
            summary = "처분 신청 수정 (MANAGER)",
            description = "작성중(WAIT) 상태의 처분 신청을 수정합니다."
    )
    @PutMapping("/{dispMId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> update(
            @PathVariable UUID dispMId,
            @Valid @RequestBody DisposalRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        disposalApplication.updateDisposal(dispMId, request, principal.getOrgCd());
        return ApiResponse.ok("수정 성공");
    }

    @Operation(
            summary = "처분 신청 삭제 (MANAGER)",
            description = "작성중(WAIT) 상태의 처분 신청을 삭제합니다."
    )
    @DeleteMapping("/{dispMId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> delete(
            @PathVariable UUID dispMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        disposalApplication.deleteDisposal(dispMId, principal.getOrgCd());
        return ApiResponse.ok("삭제 성공");
    }

    @Operation(
            summary = "처분 승인 요청 (MANAGER)",
            description = "처분 신청을 결재자(ADMIN)에게 승인 요청합니다."
    )
    @PostMapping("/{dispMId}/request")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> requestApproval(
            @PathVariable UUID dispMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        disposalApplication.requestApproval(dispMId, principal.getOrgCd());
        return ApiResponse.ok("승인 요청 완료");
    }

    @Operation(
            summary = "처분 승인 요청 취소 (MANAGER)",
            description = "승인 요청 중인 처분 신청을 취소(소프트 삭제)합니다."
    )
    @PostMapping("/{dispMId}/cancel")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> cancelRequest(
            @PathVariable UUID dispMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        disposalApplication.cancelRequest(dispMId, principal.getOrgCd());
        return ApiResponse.ok("승인 요청 취소 완료");
    }

    // TODO: 처분 승인 및 반려 구현 (ADMIN)
    @Operation(
            summary = "처분 승인 확정 (ADMIN)",
            description = "승인 요청(REQUEST) 건을 승인하여 승인(APPROVED) 상태로 만듭니다."
    )
    @PutMapping("/admin/{dispMId}/approval")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> approvalRequest(
            @PathVariable UUID dispMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        disposalApplication.approvalDisposal(
                dispMId,
                principal.getUsername(),
                principal.getOrgCd()
        );
        return ApiResponse.ok("처분 승인 확정 성공");
    }

    @Operation(
            summary = "처분 요청 반려 (ADMIN)",
            description = "승인 요청(REQUEST) 건을 반려하여 반려(REJECTED) 상태로 만듭니다."
    )
    @DeleteMapping("/admin/{dispMId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> approvalReject(
            @PathVariable UUID dispMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        disposalApplication.rejectDisposal(
                dispMId,
                principal.getUsername(),
                principal.getOrgCd()
        );
        return ApiResponse.ok("처분 요청 반려 성공");
    }
}