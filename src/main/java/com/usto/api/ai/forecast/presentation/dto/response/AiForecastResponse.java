package com.usto.api.ai.forecast.presentation.dto.response;

import java.util.List;

public record AiForecastResponse(
        Summary summary,
        List<ChartForecastPoint> chart_forecast,
        List<ChartPortfolioPoint> chart_portfolio,
        List<RecommendationRow> recommendations
) {
    public record Summary(
            String target_text,
            String risk_text,
            String period_text
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
