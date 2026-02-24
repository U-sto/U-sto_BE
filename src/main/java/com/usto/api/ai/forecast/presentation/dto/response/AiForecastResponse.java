package com.usto.api.ai.forecast.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record AiForecastResponse(
        Summary summary,
        @JsonProperty("chart_forecast")
        List<ChartForecastPoint> chartForecast,
        @JsonProperty("chart_portfolio")
        List<ChartPortfolioPoint> chartPortfolio,
        List<RecommendationRow> recommendations
) {
    public record Summary(
            @JsonProperty("target_text")
            String targetText,
            @JsonProperty("risk_text")
            String riskText,
            @JsonProperty("period_text")
            String periodText
    ) {}

    public record ChartForecastPoint(
            String label,
            Number demand,
            Number threshold
    ) {}

    public record ChartPortfolioPoint(
            String item_name,
            Number x_rul,
            Number y_importance,
            String group
    ) {}

    public record RecommendationRow(
            String item_name,
            Number quantity,
            Number budget,
            String order_date,
            String comment
    ) {}
}
