package com.usto.api.ai.forecast.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.chat.domain.model.ChatMessage;
import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.ai.chat.domain.model.SenderType;
import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;
import com.usto.api.ai.chat.infrastructure.mapper.ChatMessageMapper;
import com.usto.api.ai.chat.infrastructure.mapper.ChatThreadMapper;
import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.ai.common.AiForecastAdapter;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForecastApplication {

    private final AiForecastAdapter aiForecastAdapter;
    private final ObjectMapper objectMapper;

    @Transactional
    public AiForecastResponse analyze(String username, String orgCd, AiForecastRequest request) {
        // 1) TODO: request 필수값 검증 (year/semester/risk/message 등)

        // 2) 외부 AI 호출
        AiForecastResponse response = aiForecastAdapter.fetchForecastResponse(request);

        // 3) TODO: DB 저장 (조건 컬럼 + 결과 JSON 통 저장)
        // - summary/ts/matrix/reco 를 String으로 저장하려면:
        //   objectMapper.writeValueAsString(...) 사용

        return response;
    }
}
