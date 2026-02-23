package com.usto.api.ai.forecast.presentation.dto.request;

import com.usto.api.ai.forecast.domain.model.RiskLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AiForecastRequest(
        @NotBlank String prompt,
        @NotNull @Valid Conditions conditions
) {
    public record Conditions(
            @NotNull Integer year, //년도 (예: 2026)
            @NotNull Integer semester, //1또는2
            @NotBlank String campus, //org_cd
            @NotBlank String department, //dept_cd
            String category,          // 물품분류명 (입력 안 하면 null 또는 빈 값)
            @NotNull RiskLevel risk_level //리스크 성향 (UI의 Low/Mid/High 선택값)
    ) {}
}
