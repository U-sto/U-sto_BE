package com.usto.api.ai.forecast.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.chat.domain.model.ChatMessage;
import com.usto.api.ai.chat.domain.model.SenderType;
import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;
import com.usto.api.ai.chat.infrastructure.mapper.ChatMessageMapper;
import com.usto.api.ai.common.AiForecastAdapter;
import com.usto.api.ai.forecast.domain.model.Forecast;
import com.usto.api.ai.forecast.domain.model.RiskLevel;
import com.usto.api.ai.forecast.domain.repository.ForecastRepository;
import com.usto.api.ai.forecast.domain.service.ForecastPolicy;
import com.usto.api.ai.forecast.infrastructure.mapper.ForecastMapper;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResponse;
import com.usto.api.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastApplication {

    private final AiForecastAdapter aiForecastAdapter;
    private final ObjectMapper objectMapper;
    private final ForecastPolicy forecastPolicy;
    private final ForecastRepository forecastRepository;

    @Transactional
    public AiForecastResponse analyze(String usrId, String orgCd, AiForecastRequest request) {

        //정책 검사
        forecastPolicy.validateRequest(request,orgCd);
        forecastPolicy.validateOrganization(request.conditions().campus(),orgCd);

        // AI 호출
        AiForecastResponse aiResponse = aiForecastAdapter.fetchForecastResponse(request);

        log.info("AI Response: {}", aiResponse);

        //도메인 객체에 내용 담기
        Forecast forecast = ForecastMapper.toDomain(
                usrId,
                request.conditions().year().shortValue(),
                request.conditions().semester().byteValue(),
                request.conditions().risk_level(),
                request.prompt(),
                orgCd,
                toJsonNullable(aiResponse.summary()),
                toJsonNullable(aiResponse.chart_forecast()),
                toJsonNullable(aiResponse.chart_portfolio()),
                toJsonNullable(aiResponse.recommendations())
        );

        forecastRepository.save(forecast);

        return aiResponse;
    }

    private String toJsonNullable(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Forecast 응답 JSON 직렬화에 실패했습니다.");
        }
    }
}
