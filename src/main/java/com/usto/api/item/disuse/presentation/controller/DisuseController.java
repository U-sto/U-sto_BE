package com.usto.api.item.disuse.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.item.disuse.application.DisuseApplication;
import com.usto.api.item.disuse.presentation.dto.request.DisuseRegisterRequest;
import com.usto.api.item.disuse.presentation.dto.request.DisuseSearchRequest;
import com.usto.api.item.disuse.presentation.dto.response.DisuseItemListResponse;
import com.usto.api.item.disuse.presentation.dto.response.DisuseListResponse;
import com.usto.api.item.disuse.presentation.dto.response.DisuseRegisterResponse;
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

@Tag(name = "item-disuse-controller", description = "물품 불용 관리 API")
@RestController
@RequestMapping("/api/item/disuses")
@RequiredArgsConstructor
public class DisuseController {

    private final DisuseApplication disuseApplication;

    @Operation(
            summary = "불용등록목록 조회 (페이징)",
            description = "불용 신청 마스터 목록을 페이징하여 조회합니다. 기본 30개씩 조회됩니다."
    )
    @GetMapping
    public ApiResponse<Page<DisuseListResponse>> getList(
            @Valid DisuseSearchRequest searchRequest,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "30")
            @RequestParam(defaultValue = "30") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("creAt").ascending());

        return ApiResponse.ok("조회 성공",
                disuseApplication.getDisuseList(searchRequest, principal.getOrgCd(), pageable));
    }

    @Operation(
            summary = "불용물품목록 조회 (페이징)",
            description = "특정 불용 신청의 상세 물품 목록을 페이징하여 조회합니다."
    )
    @GetMapping("/{dsuMId}/items")
    public ApiResponse<Page<DisuseItemListResponse>> getItems(
            @PathVariable UUID dsuMId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "30")
            @RequestParam(defaultValue = "30") int size,
            @AuthenticationPrincipal UserPrincipal principal) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("itmNo").ascending());

        return ApiResponse.ok("조회 성공",
                disuseApplication.getDisuseItems(dsuMId, principal.getOrgCd(), pageable));
    }

    @Operation(
            summary = "불용 신청 등록 (MANAGER)",
            description = "반납(RTN) 상태의 물품을 불용 신청합니다. 여러 물품을 한 번에 신청할 수 있습니다."
    )
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<DisuseRegisterResponse> register(
            @Valid @RequestBody DisuseRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID dsuMId = disuseApplication.registerDisuse(
                request,
                principal.getUsername(),
                principal.getOrgCd()
        );
        return ApiResponse.ok("불용 신청 등록 성공", new DisuseRegisterResponse(dsuMId));
    }

    @Operation(
            summary = "불용 신청 수정 (MANAGER)",
            description = "작성중(WAIT) 상태의 불용 신청을 수정합니다."
    )
    @PutMapping("/{dsuMId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> update(
            @PathVariable UUID dsuMId,
            @Valid @RequestBody DisuseRegisterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        disuseApplication.updateDisuse(dsuMId, request, principal.getOrgCd());
        return ApiResponse.ok("수정 성공");
    }

    @Operation(
            summary = "불용 신청 삭제 (MANAGER)",
            description = "작성중(WAIT) 상태의 불용 신청을 삭제합니다."
    )
    @DeleteMapping("/{dsuMId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> delete(
            @PathVariable UUID dsuMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        disuseApplication.deleteDisuse(dsuMId, principal.getOrgCd());
        return ApiResponse.ok("삭제 성공");
    }

    @Operation(
            summary = "불용 승인 요청 (MANAGER)",
            description = "불용 신청을 결재자(ADMIN)에게 승인 요청합니다."
    )
    @PostMapping("/{dsuMId}/request")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> requestApproval(
            @PathVariable UUID dsuMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        disuseApplication.requestApproval(dsuMId, principal.getOrgCd());
        return ApiResponse.ok("승인 요청 완료");
    }

    @Operation(
            summary = "불용 승인 요청 취소 (MANAGER)",
            description = "승인 요청 중인 불용 신청을 취소(소프트 삭제)합니다."
    )
    @PostMapping("/{dsuMId}/cancel")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> cancelRequest(
            @PathVariable UUID dsuMId,
            @AuthenticationPrincipal UserPrincipal principal) {
        disuseApplication.cancelRequest(dsuMId, principal.getOrgCd());
        return ApiResponse.ok("승인 요청 취소 완료");
    }

    // TODO: 불용 승인 및 반려 구현 (ADMIN)
}