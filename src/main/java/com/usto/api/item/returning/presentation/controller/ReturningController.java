package com.usto.api.item.returning.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.item.returning.application.ReturningApplication;
import com.usto.api.item.returning.presentation.dto.request.ReturningRegisterRequest;
import com.usto.api.item.returning.presentation.dto.request.ReturningSearchRequest;
import com.usto.api.item.returning.presentation.dto.response.ReturningItemListResponse;
import com.usto.api.item.returning.presentation.dto.response.ReturningListResponse;
import com.usto.api.item.returning.presentation.dto.response.ReturningRegisterResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "item-returning-controller", description = "물품 반납 관리 API")
@RestController
@RequestMapping("/api/item/returnings")
@RequiredArgsConstructor
public class ReturningController {

    private final ReturningApplication returningApplication;

    @Operation(
            summary = "반납등록목록 조회",
            description = "반납 신청 마스터 목록을 조회합니다."
    )
    @GetMapping
    public ApiResponse<List<ReturningListResponse>> getList(
            @Valid ReturningSearchRequest searchRequest,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("조회 성공",
                returningApplication.getReturningList(searchRequest, principal.getOrgCd()));
    }

    @Operation(
            summary = "반납물품목록 조회",
            description = "특정 반납 신청의 상세 물품 목록을 조회합니다."
    )
    @GetMapping("/{rtrnMId}/items")
    public ApiResponse<List<ReturningItemListResponse>> getItems(
            @PathVariable UUID rtrnMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("조회 성공",
                returningApplication.getReturningItems(rtrnMId, principal.getOrgCd()));
    }

    @Operation(
            summary = "반납 신청 등록 (MANAGER)",
            description = "운용 중인 물품을 반납 신청합니다. 여러 물품을 한 번에 신청할 수 있습니다."
    )
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<ReturningRegisterResponse> register(
            @Valid @RequestBody ReturningRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID rtrnMId = returningApplication.registerReturning(
                request,
                principal.getUsername(),
                principal.getOrgCd()
        );
        return ApiResponse.ok("반납 신청 등록 성공", new ReturningRegisterResponse(rtrnMId));
    }

    @Operation(
            summary = "반납 신청 수정 (MANAGER)",
            description = "작성중(WAIT) 상태의 반납 신청을 수정합니다."
    )
    @PutMapping("/{rtrnMId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> update(
            @PathVariable UUID rtrnMId,
            @Valid @RequestBody ReturningRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        returningApplication.updateReturning(rtrnMId, request, principal.getOrgCd());
        return ApiResponse.ok("수정 성공");
    }

    @Operation(
            summary = "반납 신청 삭제 (MANAGER)",
            description = "작성중(WAIT) 상태의 반납 신청을 삭제합니다."
    )
    @DeleteMapping("/{rtrnMId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> delete(
            @PathVariable UUID rtrnMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        returningApplication.deleteReturning(rtrnMId, principal.getOrgCd());
        return ApiResponse.ok("삭제 성공");
    }

    @Operation(
            summary = "반납 승인 요청 (MANAGER)",
            description = "반납 신청을 결재자(ADMIN)에게 승인 요청합니다."
    )
    @PostMapping("/{rtrnMId}/request")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> requestApproval(
            @PathVariable UUID rtrnMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        returningApplication.requestApproval(rtrnMId, principal.getOrgCd());
        return ApiResponse.ok("승인 요청 완료");
    }

    @Operation(
            summary = "반납 승인 요청 취소 (MANAGER)",
            description = "승인 요청 중인 반납 신청을 취소(소프트 삭제)합니다."
    )
    @PostMapping("/{rtrnMId}/cancel")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> cancelRequest(
            @PathVariable UUID rtrnMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        returningApplication.cancelRequest(rtrnMId, principal.getOrgCd());
        return ApiResponse.ok("승인 요청 취소 완료");
    }

    // TODO: 반납 승인 및 반려 구현 (ADMIN)
}