package com.usto.api.ai.forecast.presentation.controller;

import com.usto.api.ai.forecast.application.ForecastApplication;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastGetResponse;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResponse;
import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Tag(name = "[08-01] forecast-controller", description = "사용주기 AI예측 API")
@RestController
@RequestMapping("/api/ai/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastApplication forecastApplication;

    @Operation(
            summary = "통계 예측 분석",
            description = "조건(년도/학기/캠퍼스/학과/카테고리/리스크) 기반으로 수요 예측 및 조달 권고안을 생성합니다.\n" +
                    "category는 null이거나 정해진 것만 들어가야합니다."
    )
    @PostMapping
    public ApiResponse<AiForecastResponse> analyze(
            @RequestBody @Valid AiForecastRequest request,
            @Valid @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        AiForecastResponse data = forecastApplication.analyze(
                userPrincipal.getUsername(),
                userPrincipal.getOrgCd(),
                request
        );

        return ApiResponse.ok("예측 분석 성공", data);
    }

    @Operation(
            summary = "기록 조회",
            description = "이전 기록을 조회합니다"
    )
    @GetMapping()
    public ApiResponse<List<AiForecastGetResponse>> find(
            @Valid @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<AiForecastGetResponse> response = forecastApplication.findAll(
                userPrincipal.getUsername(),
                userPrincipal.getOrgCd()
        );

        return ApiResponse.ok("조회 성공",response);
    }

    @Operation(
            summary = "기록 내용 확인",
            description = "이전 기록 내용을 확인합니다"
    )
    @GetMapping("contents/{forecastId}")
    public ApiResponse<AiForecastResponse> check(
            @RequestParam @Valid UUID forecastId,
            @Valid @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        AiForecastResponse response = forecastApplication.check(
                userPrincipal.getUsername(),
                userPrincipal.getOrgCd(),
                forecastId
        );


        return ApiResponse.ok("조회 성공",response);
    }

    @Operation(
            summary = "기록 이름 수정",
            description = "이전 기록 이름을 수정합니다"
    )
    @PatchMapping("/{forecastId}")
    public ApiResponse<?> updateTitle(
            @Valid @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid String newTitle,
            @RequestParam @Valid UUID forecastId

    ) {
        forecastApplication.updateTitle(
                userPrincipal.getUsername(),
                userPrincipal.getOrgCd(),
                newTitle,
                forecastId
        );

        return ApiResponse.ok("기록 이름 수정 성공");
    }

    @Operation(
            summary = "기록 삭제",
            description = "이전 기록을 삭제합니다"
    )
    @DeleteMapping()
    public ApiResponse<?> delete(
            @Valid @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam @Valid UUID forecastId

            ) {
        forecastApplication.delete(
                userPrincipal.getUsername(),
                userPrincipal.getOrgCd(),
                forecastId
        );

        return ApiResponse.ok("삭제 성공");
    }
}
