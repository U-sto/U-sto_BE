package com.usto.api.ai.forecast.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record AiForecastResponse(
        @JsonProperty("section_1_time_series")
        List<TimeSeriesPoint> section1TimeSeries,

        @JsonProperty("section_2_portfolio")
        List<PortfolioPoint> section2Portfolio,

        @JsonProperty("section_3_recommendations")
        List<RecommendationItem> section3Recommendations
) {
    public record TimeSeriesPoint(
            @JsonProperty("month")
            Integer month,
            @JsonProperty("quantity")
            Number quantity,
            @JsonProperty("is_rop")
            Boolean isRop
    ) {
    }

    public record PortfolioPoint(
            @JsonProperty("item_name")
            String itemName,
            @JsonProperty("x_rul")
            Number xRul,
            @JsonProperty("y_importance")
            Number yImportance
    ) {
    }

    public record RecommendationItem(
            Long id,
            @JsonProperty("item_name")
            String itemName,
            Number quantity,
            @JsonProperty("estimated_budget")
            Number estimatedBudget,
            @JsonProperty("recommend_order_date")
            String recommendOrderDate,
            @JsonProperty("ai_insight")
            AiInsight aiInsight
    ) {
    }

    public record AiInsight(
            @JsonProperty("report_title")
            String reportTitle,
            @JsonProperty("analysis_summary")
            String analysisSummary,
            @JsonProperty("action_item")
            String actionItem,
            @JsonProperty("alert_level")
            String alertLevel
    ) {
    }
}
