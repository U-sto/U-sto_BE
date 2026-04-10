package com.usto.api.ai.forecast.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record AiForecastResponse(
        SectionTimeSeries section1TimeSeries,
        StrategicGuide section2StrategicGuide,
        List<RecommendationItem> section3Recommendations,
        AlgorithmGuide section4AlgorithmGuide
) {
    @Builder
    public record SectionTimeSeries(
            List<MonthlyForecastPoint> monthlyPoints, //is_rop = false
            List<RopPoint> ropPoints //is_rop = true
    ) {
    }

    @Builder
    public record MonthlyForecastPoint(
            Integer month,
            Integer quantity
    ) {
    }

    @Builder
    public record RopPoint(
            Integer month,
            Integer quantity,
            String ropDate,
            Integer baseQty,
            Integer safetyStock,
            Integer totalOrderQty
    ) {
    }
    @Builder
    public record StrategicGuide(
            String aiSummaryComment,
            String smartForecasting,
            String timeToProcure,
            String budgetGuide
    ) {
    }
    @Builder
    public record RecommendationItem(
            Long id,
            String itemName,
            Integer quantity,
            Long estimatedBudget,
            String recommendOrderDate
    ) {
    }
    @Builder
    public record AlgorithmGuide(
            String formula1,
            String formula2,
            String formula3
    ) {
    }
}
