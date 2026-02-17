package com.usto.api.ai.chat.application;

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

        // 1. DTO 구성 (우리가 정의한 threadId -> session_id 매핑 활용)
        AiChatRequest request = new AiChatRequest(threadId, message);

        /*
        // [비즈니스 로직 확장 가능성]
        // 2. Thread 조회 또는 생성 (DB)
        ChatThread thread = (threadId == null)
                ? createNewThread(username, message)
                : validateAndGetThread(username, threadId);

        // 3. 사용자 메시지 선행 저장
        saveMessage(thread, "USER", message, null);
        */

        // 4. 진짜 AI 클라이언트 호출 (Adapter 사용)
        AiChatResponse aiResponse = aiClientAdapter.fetchChatResponse(request);

        /*
        // 5. AI 응답 및 참고문헌(References) 저장
        // 팀원 협의에 대비해 references 리스트를 JSON 문자열로 변환하여 저장
        try {
            String refJson = objectMapper.writeValueAsString(aiResponse.references());
            saveMessage(thread, "BOT", aiResponse.replyMessage(), refJson);
        } catch (JsonProcessingException e) {
            log.error("참고문헌 데이터 변환 실패", e);
        }
        */

        return aiResponse;
    }
/*
    private ChatThread createNewThread(String username, String firstMessage) {
        String title = firstMessage.length() > 20 ? firstMessage.substring(0, 20) : firstMessage;
        return threadRepository.save(ChatThread.builder()
                .userId(username)
                .title(title)
                .build());
    }

    private ChatThread validateAndGetThread(String username, Long threadId) {
        ChatThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대화방입니다."));

        if (!thread.getUserId().equals(username)) {
            throw new SecurityException("접근 권한이 없습니다.");
        }
        return thread;
    }

    private void saveMessage(ChatThread thread, String senderType, String content) {
        messageRepository.save(ChatMessage.builder()
                .thread(thread)
                .senderType(senderType)
                .content(content)
                .build());

         */

    @Transactional
    public AiChatResponse testSend(String message, UUID threadId) {
        return aiClientAdapter.fetchChatResponse(new AiChatRequest(threadId, message));
    }
}

