package com.usto.api.item.asset.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.item.asset.application.AssetApplication;
import com.usto.api.item.asset.presentation.dto.request.AssetListForPrintRequest;
import com.usto.api.item.asset.presentation.dto.request.AssetPrintRequest;
import com.usto.api.item.asset.presentation.dto.request.AssetSearchRequest;
import com.usto.api.item.asset.presentation.dto.request.AssetUpdateRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetDetailResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetListForPrintResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetListResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Tag(name = "item-asset-controller", description = "물품 대장 관리 API")
@RestController
@RequestMapping("/api/item/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetApplication assetApplication;

    @Operation(
            summary = "물품대장 조회",
            description = "필터 조건(G2B, 취득일자, 정리일자, 부서, 운용상태, 물품번호)에 따라 운용대장을 조회합니다. 논리삭제된 물품은 제외됩니다."
    )
    @GetMapping
    public ApiResponse<Page<AssetListResponse>> getList(
            @Valid AssetSearchRequest searchRequest,
            @PageableDefault(size = 30) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ApiResponse.ok("조회 성공",
                assetApplication.getAssetList(searchRequest, principal.getOrgCd(), pageable));
    }

    @Operation(
            summary = "개별 물품 상세 조회",
            description = "물품고유번호로 개별 물품의 상세 정보와 상태 이력을 조회합니다."
    )
    @GetMapping("/{itmNo}")
    public ApiResponse<AssetDetailResponse> getDetail(
            @Parameter(description = "물품고유번호", example = "M202600001")
            @PathVariable String itmNo,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("조회 성공",
                assetApplication.getAssetDetail(itmNo, principal.getOrgCd()));
    }

    @Operation(
            summary = "개별 물품 정보 수정",
            description = "취득단가, 내용연수, 비고를 수정합니다. 삭제되었거나 불용(DSU) 상태인 물품은 수정할 수 없습니다."
    )
    @PatchMapping("/{itmNo}")
    public ApiResponse<Void> updateAsset(
            @Parameter(description = "물품고유번호", example = "M202600001")
            @PathVariable String itmNo,
            @Valid @RequestBody AssetUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        assetApplication.updateAssetInfo(itmNo, request, principal.getOrgCd());
        return ApiResponse.ok("수정 성공");
    }

    @Operation(
            summary = "물품 QR 라벨 출력",
            description = "선택된 물품들의 QR 코드 라벨을 PDF로 생성하여 다운로드합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "PDF 파일 다운로드 성공",
            content = @Content(
                    mediaType = "application/pdf",
                    schema = @Schema(type = "string", format = "binary")
            )
    )
    @PostMapping("/print")
    public ResponseEntity<byte[]> printQrLabels(
            @Valid @RequestBody AssetPrintRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        byte[] pdfBytes = assetApplication.generateQrLabelsPdf(
                request.getItmNos(),
                principal.getOrgCd()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("asset_qr_labels_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".pdf")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @Operation(
            summary = "출력물관리 목록 조회 (페이징)",
            description = "필터 조건(G2B, 취득일자, 정리일자, 부서, 운용상태, 물품번호 + 출력상태)에 따라 운용대장을 조회합니다. 논리삭제된 물품은 제외됩니다."
    )
    @GetMapping("/print")
    public ApiResponse<Page<AssetListForPrintResponse>> getPrintList(
            @Valid AssetListForPrintRequest searchRequest,
            @PageableDefault(size = 30) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ApiResponse.ok("조회 성공",
                assetApplication.getAssetListForPrint(searchRequest, principal.getOrgCd(), pageable));
    }
}