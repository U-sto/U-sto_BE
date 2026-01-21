package com.usto.api.g2b.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.g2b.application.G2bSyncServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "g2b-controller", description = "G2B API")
@RestController
@RequestMapping("/api/g2b")
@RequiredArgsConstructor
public class G2BSyncController {

    private final G2bSyncServiceImpl g2bSyncServiceImpl;

    @Operation(
            summary = "G2B 목록정보 최신화"
    )
    @PutMapping("/sync")
    public ApiResponse<?> sync() {
        int updatedCount = g2bSyncServiceImpl.syncLatest();
        return ApiResponse.ok(
                "G2B 동기화 완료. 업데이트된 품목 수: " + updatedCount + "건"
        );
    }
}
