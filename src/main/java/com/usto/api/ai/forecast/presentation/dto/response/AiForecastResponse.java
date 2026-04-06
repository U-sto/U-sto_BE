package com.usto.api.ai.forecast.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record AiForecastResponse(
        @JsonProperty("section_1_time_series")
        List<TimeSeriesPoint> section1TimeSeries,

        @JsonProperty("section_2_strategic_guide")
        StrategicGuidePoint section2StrategicGuide,

        @JsonProperty("section_3_recommendations")
        List<RecommendationItem> section3Recommendations,

        @JsonProperty("section_4_algorithm_guide")
        AlgorithmGuide section4AlgorithmGuide
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

    public record StrategicGuidePoint(
            @JsonProperty("ai_summary_comment")
            String aiSummaryComment,
            @JsonProperty("smart_forecasting")
            String smartForecasting,
            @JsonProperty("time_to_procure")
            String timeToProcure,
            @JsonProperty("budget_guide")
            String budgetGuide

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

            public record AlgorithmGuide(
            @JsonProperty("formula_1")
            String formula1,
            @JsonProperty("formula_2")
            String formula2,
            @JsonProperty("formula_3")
            String formula3
    ) {}
}
