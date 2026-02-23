package com.usto.api.ai.forecast.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.common.AiForecastAdapter;
import com.usto.api.ai.forecast.domain.model.Forecast;
import com.usto.api.ai.forecast.domain.repository.ForecastRepository;
import com.usto.api.ai.forecast.domain.service.ForecastPolicy;
import com.usto.api.ai.forecast.infrastructure.mapper.ForecastMapper;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResponse;
import com.usto.api.common.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
        log.info("summary : {}",aiResponse.summary());

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
                toJsonNullable(aiResponse.recommendations()),
                request.conditions().department()
        );

        forecastRepository.save(forecast);

        return aiResponse;
    }

    public AiForecastResponse check(String username, String orgCd, @Valid UUID forecastId) {

        Forecast forecast = forecastRepository.findById(forecastId);
        if(forecast == null){
            throw new BusinessException("존재하지 않는 예측입니다.");
        }

        forecastPolicy.validateOrganization(forecast.getOrgCode(),orgCd);
        forecastPolicy.valdateOwnerShip(forecast.getUserId(),username);

        JsonNode summaryNode = readTreeOrNull(forecast.getSummaryJson());
        JsonNode tsNode = readTreeOrNull(forecast.getTsJson());
        JsonNode matrixNode = readTreeOrNull(forecast.getMatrixJson());
        JsonNode recoNode = readTreeOrNull(forecast.getRecoJson());

        return AiForecastResponse
                .builder()
                .summary(objectMapper.convertValue(summaryNode, AiForecastResponse.Summary.class))
                .chart_forecast(objectMapper.convertValue(tsNode, new TypeReference<>() {}))
                .chart_portfolio(objectMapper.convertValue(matrixNode, new TypeReference<>() {}))
                .recommendations(objectMapper.convertValue(recoNode, new TypeReference<>() {}))
                .build();
    }

    private String toJsonNullable(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Forecast 응답 JSON 직렬화에 실패했습니다.");
        }
    }

    private JsonNode readTreeOrNull(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            return null;
        }
    }


    public List<UUID> findAll(String username, String orgCd) {

        List<UUID> ids = forecastRepository.findByUsrId(username);

        return ids;
    }
}
