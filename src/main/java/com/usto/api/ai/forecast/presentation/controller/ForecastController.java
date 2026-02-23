package com.usto.api.ai.forecast.presentation.controller;

import com.usto.api.ai.forecast.application.ForecastApplication;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResponse;
import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/ai/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastApplication forecastApplication;

    @Operation(
            summary = "통계 예측 분석",
            description = "조건(년도/학기/캠퍼스/학과/카테고리/리스크) 기반으로 수요 예측 및 조달 권고안을 생성합니다."
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
    //기록 조회
    //조회한 기록 내용 확인
}
