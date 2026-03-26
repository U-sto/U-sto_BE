package com.usto.api.ai.forecast.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.usto.api.ai.forecast.domain.model.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AiForecastRequestToAi(

        @Schema(example = "분석해줘")
        @NotBlank String prompt,
        @NotNull @Valid Conditions conditions
) {
    public record Conditions(

            @Schema(example = "2026")
            @NotNull Integer year, //년도 (예: 2026)

            @Schema(example = "1")
            @NotNull String semester, //1=1학기,2=여름학기,3=2학기,4=겨울학기

            @JsonProperty("org_cd")
            @Schema(example = "7008277")
            @NotBlank String campus, //org_cd

            @JsonProperty("dept_name")
            @JsonAlias({"department"})
            String deptName, //소프트웨어융합대학행정실

            @Schema(example = "")
            String category,          // 물품분류명 (입력 안 하면 null 또는 빈 값)

            @JsonProperty("risk_level")
            @Schema(example = "Low")
            @NotNull RiskLevel riskLevel //리스크 성향 (UI의 Low/Medium/High 선택값)
    ) {}
}
