package com.usto.api.g2b.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.g2b.application.G2bInitServiceImpl;
import com.usto.api.g2b.application.G2bSyncServiceImpl;
import com.usto.api.g2b.presentation.dto.response.ShoppingMallEnvelope;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "g2b-controller", description = "G2B API")
@RestController
@RequestMapping("/api/g2b")
@RequiredArgsConstructor
public class G2bSyncController {

    private final G2bSyncServiceImpl g2bSyncServiceImpl;
    private final G2bInitServiceImpl g2bInitServiceImpl;

    @Operation(
            summary = "G2B 목록정보 최신화(자동)"
    )
    @PutMapping("/sync")
    public ApiResponse<?> sync() {
        int updatedCount = g2bSyncServiceImpl.syncLatest();
        if (updatedCount == 0) {
            return ApiResponse.ok("조회 결과가 없습니다.(이미 최신 상태입니다)");
        }
        return ApiResponse.ok(
                "G2B 동기화 완료. 업데이트된 품목 수: " + updatedCount + "건"
        );
    }

    @Operation(
            summary = "G2B 초기데이터 최신화(수동)"
    )
    @PutMapping("/init")
    public ApiResponse<?> getPublicItemList(
    ){
        LocalDate seedDate = LocalDate.of(2025, 8, 18);
        int updatedCount = g2bInitServiceImpl.syncInit1y(seedDate);
        if (updatedCount == 0) {
            return ApiResponse.ok("서버 오류가 발생했거나, 이미 세팅이 완료됐습니다.");
        }
        return ApiResponse.ok("초기 세팅 성공");
    }
}
