package com.usto.api.ai.chat.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.ai.common.AiClientAdapter;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatApplication {

    //private final ChatThreadRepository threadRepository;
    //private final ChatMessageRepository messageRepository;
    private final AiClientAdapter aiClientAdapter; // 진짜 AI 서버와 통신하는 어댑터
    private final ObjectMapper objectMapper;

    @Transactional
    public AiChatResponse send(String username, String message, UUID threadId) {
        // 1. 요청 생성
        AiChatRequest request = new AiChatRequest(threadId, message);
        log.info("User {} asked: {}", username, message);

        // 2. AI 서버 호출 (어댑터 사용)
        AiChatResponse aiResponse = aiClientAdapter.fetchChatResponse(request);

        // 3. 이력 저장 로직 (AiChatApplicationService에 있던 로직을 이쪽으로 통합)
        try {
            if (aiResponse.references() != null) {
                String refJson = objectMapper.writeValueAsString(aiResponse.references());
                log.info("AI Response References saved as JSON: {}", refJson);

                // TODO: messageRepository.save(...) 를 통해 DB에 질문과 답변, 참고문헌(JSON)을 저장
                // 이 단계가 '이력 테이블 자동 기록' 원칙을 실현하는 부분
            }
        } catch (JsonProcessingException e) {
            log.error("참고문헌 데이터 변환 실패", e);
        }

        return aiResponse;
    }

    @Transactional
    public AiChatResponse testSend(String message, UUID threadId) {
        return aiClientAdapter.fetchChatResponse(new AiChatRequest(threadId, message));
    }
}

