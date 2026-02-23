package com.usto.api.ai.forecast.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.common.AiForecastAdapter;
import com.usto.api.ai.forecast.domain.service.ForecastPolicy;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForecastApplication {

    private final AiForecastAdapter aiForecastAdapter;
    private final ObjectMapper objectMapper;
    private final ForecastPolicy forecastPolicy;

    @Transactional
    public AiForecastResponse analyze(String usrId, String orgCd, AiForecastRequest request) {
        // 정책 검사
        forecastPolicy.validateRequest(request,orgCd);
        forecastPolicy.validateOrganization(request.conditions().campus(),orgCd);

        // AI 호출
        AiForecastResponse response = aiForecastAdapter.fetchForecastResponse(request);

        // 3) TODO: DB 저장 (조건 컬럼 + 결과 JSON 통 저장)
        // - summary/ts/matrix/reco 를 String으로 저장하려면:
        //   objectMapper.writeValueAsString(...) 사용

        return response;
    }
}
