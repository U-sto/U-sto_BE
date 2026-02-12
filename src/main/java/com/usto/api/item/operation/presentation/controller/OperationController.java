package com.usto.api.item.operation.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.item.operation.application.OperationApplication;
import com.usto.api.item.operation.presentation.dto.request.OperationRegisterRequest;
import com.usto.api.item.operation.presentation.dto.request.OperationSearchRequest;
import com.usto.api.item.operation.presentation.dto.response.OperationItemListResponse;
import com.usto.api.item.operation.presentation.dto.response.OperationListResponse;
import com.usto.api.item.operation.presentation.dto.response.OperationRegisterResponse;
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

@Tag(name = "item-operation-controller", description = "물품 운용 관리 API")
@RestController
@RequestMapping("/api/item/operations")
@RequiredArgsConstructor
public class OperationController {

    private final OperationApplication operationApplication;

    @Operation(
            summary = "운용등록목록 조회 (페이징)",
            description = "운용 등록 마스터 목록을 페이징하여 조회합니다. 기본 30개씩 조회됩니다."
    )
    @GetMapping
    public ApiResponse<Page<OperationListResponse>> getList(
            @Valid OperationSearchRequest searchRequest,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "30")
            @RequestParam(defaultValue = "30") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("creAt").ascending());

        return ApiResponse.ok("조회 성공",
                operationApplication.getOperationList(searchRequest, principal.getOrgCd(), pageable));
    }

    @Operation(
            summary = "운용물품목록 조회 (페이징)",
            description = "특정 운용 등록의 상세 물품 목록을 페이징하여 조회합니다."
    )
    @GetMapping("/{operMId}/items")
    public ApiResponse<Page<OperationItemListResponse>> getItems(
            @PathVariable UUID operMId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "30")
            @RequestParam(defaultValue = "30") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("itmNo").ascending());

        return ApiResponse.ok("조회 성공",
                operationApplication.getOperationItems(operMId, principal.getOrgCd(), pageable));
    }

    @Operation(
            summary = "운용 신청 등록 (MANAGER)",
            description = "취득 또는 반납 상태의 물품을 운용 신청합니다. 여러 물품을 한 번에 신청할 수 있습니다."
    )
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<OperationRegisterResponse> register(
            @Valid @RequestBody OperationRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID operMId = operationApplication.registerOperation(
                request,
                principal.getUsername(),
                principal.getOrgCd()
        );
        return ApiResponse.ok("운용 신청 등록 성공", new OperationRegisterResponse(operMId));
    }

    @Operation(
            summary = "운용 신청 수정 (MANAGER)",
            description = "작성중(WAIT) 상태의 운용 신청을 수정합니다."
    )
    @PatchMapping("/{operMId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> update(
            @PathVariable UUID operMId,
            @Valid @RequestBody OperationRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        operationApplication.updateOperation(operMId, request, principal.getOrgCd());
        return ApiResponse.ok("수정 성공");
    }

    @Operation(
            summary = "운용 신청 삭제 (MANAGER)",
            description = "작성중(WAIT) 상태의 운용 신청을 삭제합니다."
    )
    @DeleteMapping("/{operMId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> delete(
            @PathVariable UUID operMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        operationApplication.deleteOperation(operMId, principal.getOrgCd());
        return ApiResponse.ok("삭제 성공");
    }

    @Operation(
            summary = "운용 승인 요청 (MANAGER)",
            description = "운용 등록을 결재자(ADMIN)에게 승인 요청합니다."
    )
    @PostMapping("/{operMId}/request")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> requestApproval(
            @PathVariable UUID operMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        operationApplication.requestApproval(operMId, principal.getOrgCd());
        return ApiResponse.ok("승인 요청 완료");
    }

    @Operation(
            summary = "운용 승인 요청 취소 (MANAGER)",
            description = "승인 요청 중인 운용 등록을 취소(소프트 삭제)합니다."
    )
    @PostMapping("/{operMId}/cancel")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> cancelRequest(
            @PathVariable UUID operMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        operationApplication.cancelRequest(operMId, principal.getOrgCd());
        return ApiResponse.ok("승인 요청 취소 완료");
    }

    // TODO: 운용 승인 및 반려 구현 (ADMIN)
}