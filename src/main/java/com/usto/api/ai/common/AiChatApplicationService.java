package com.usto.api.ai.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.chat.domain.repository.ChatMessageRepository;
import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatApplicationService {

    private final AiClientAdapter aiClientAdapter;
    private final ObjectMapper objectMapper;

    @Transactional
    public AiChatResponse askToAi(AiChatRequest request, String userId) {
        log.info("User {} asked: {}", userId, request.message());

        // 1. AI 서버 호출 (OpenAI Key를 헤더에 실어 전송)
        AiChatResponse response = aiClientAdapter.fetchChatResponse(request);

        // 2. AI 응답 및 참고문헌(References) 저장 로직 (필요시 활성화)
        try {
            String refJson = objectMapper.writeValueAsString(response.references());
            log.info("AI Response References: {}", refJson);
        } catch (JsonProcessingException e) {
            log.error("References 직렬화 실패", e);
        }

        return response;
    }
}
