package com.usto.api.ai.forecast.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiForecastResponse(
        @JsonProperty("section_1_time_series")
        List<TimeSeriesPointRaw> section1TimeSeries,

        @JsonProperty("section_2_strategic_guide")
        StrategicGuidePointRaw  section2StrategicGuide,

        @JsonProperty("section_3_recommendations")
        List<RecommendationItemRaw> section3Recommendations,

        @JsonProperty("section_4_algorithm_guide")
        AlgorithmGuideRaw  section4AlgorithmGuide
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TimeSeriesPointRaw(
            @JsonProperty("month")
            Integer month, //월 (1~12)
            @JsonProperty("quantity")
            Number quantity, //해당 월의 예측 고장 수량 (막대 그래프 렌더링용)
            @JsonProperty("is_rop")
            Boolean isRop, //실제 조치해야 하는 권장 발주월 여부 (True일 경우  툴팁 및 점선 마커 표시)
            @JsonProperty("rop_date")
            String ropDate, //툴팁용 정확한 권장 발주 날짜 (is_rop가 true일 때만 포함)
            @JsonProperty("base_qty")
            Number baseQty, //툴팁용 기간 내 총 고장 예상 수량 (is_rop가 true일 때만 포함)
            @JsonProperty("safety_stock")
            Number safetyStock, //툴팁용 대비용 안전 재고 (is_rop가 true일 때만 포함)
            @JsonProperty("total_order_qty")
            Number totalOrderQty //점선 그래프 Y축 렌더링용 총 발주 권장량 (base_qty + safety_stock)

    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StrategicGuidePointRaw(
            @JsonProperty("ai_summary_comment")
            String aiSummaryComment, //LLM이 작성한 1~2문장의 직관적인 요약 코멘트
            @JsonProperty("smart_forecasting")
            String smartForecasting, //스마트 수요 예측 (서비스 수준 및 총 필요수량 안내)
            @JsonProperty("time_to_procure")
            String timeToProcure, //적기 조달 시점 (리드타임 역산 결과 및 발주 기한 안내)
            @JsonProperty("budget_guide")
            String budgetGuide //필요 예산 가이드 (천 원 단위 포맷팅된 문자열)

    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RecommendationItemRaw(
            @JsonProperty("id")
            Long id,
            @JsonProperty("item_name")
            String itemName,
            @JsonProperty("quantity")
            Number quantity, //총 권장 발주 수량 (고장예상수량 + 안전재고)
            @JsonProperty("estimated_budget")
            Number estimatedBudget, //예상 예산 (총합)
            @JsonProperty("recommend_order_date")
            String recommendOrderDate //권장 발주 마감 기한
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AlgorithmGuideRaw(
            @JsonProperty("formula_1")
            String formula1, //적정 권장 수량 도출 공식 설명
            @JsonProperty("formula_2")
            String formula2, //발주 시점(ROP) 도출 공식 설명
            @JsonProperty("formula_3")
            String formula3 //잔여 수명(RUL) 정의 설명
    ) {}
}
