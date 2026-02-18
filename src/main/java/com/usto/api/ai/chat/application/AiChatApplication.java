package com.usto.api.ai.chat.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.chat.domain.model.ChatMessage;
import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.ai.chat.domain.model.SenderType;
import com.usto.api.ai.chat.domain.repository.ChatMessageRepository;
import com.usto.api.ai.chat.domain.repository.ChatThreadRepository;
import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;
import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;
import com.usto.api.ai.chat.infrastructure.mapper.ChatMessageMapper;
import com.usto.api.ai.chat.infrastructure.mapper.ChatThreadMapper;
import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.ai.common.AiClientAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatApplication {

    private final AiClientAdapter aiClientAdapter;
    private final ObjectMapper objectMapper;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatThreadRepository chatThreadRepository;


    @Transactional
    public AiChatResponse send(String userid,String orgCd, String message, UUID threadId) {

        if(threadId == null){
            String title = sumMsg(message);
            ChatThread master = ChatThreadMapper.toDomain(userid,title,orgCd);

            ChatThreadJpaEntity maseterEntity = ChatThreadMapper.toEntity(master);
            chatThreadRepository.save(maseterEntity);

            UUID masterId = master.getThreadId();
            threadId= masterId;
        }

        ChatMessage chatMessageByUser = ChatMessageMapper.toDomain(
                threadId,
                message,
                SenderType.USER,
                null,
                orgCd
        );
        ChatMessageJpaEntity entityByUser = ChatMessageMapper.toEntity(chatMessageByUser);
        chatMessageRepository.save(entityByUser);

        AiChatRequest request = new AiChatRequest(threadId, message);
        AiChatResponse aiResponse = aiClientAdapter.fetchChatResponse(request);

        if (aiResponse == null) {
            log.error("챗봇에게서 답장을 받지 못함. 저장 스킵 threadId: {}", threadId);
            return null;
        }

        log.info("AI Response: {}", aiResponse);

        try {
            if (aiResponse.references() != null) {
                String replyJson = aiResponse.reply();
                String refJson = objectMapper.writeValueAsString(aiResponse.references());

                ChatMessage chatMessageByBot = ChatMessageMapper.toDomain(
                        threadId,
                        replyJson,
                        SenderType.AI_BOT,
                        refJson,
                        orgCd
                );
                ChatMessageJpaEntity entityByBot = ChatMessageMapper.toEntity(chatMessageByBot);
                chatMessageRepository.save(entityByBot);
            }else {
                String replyJson = objectMapper.writeValueAsString(aiResponse.reply());

                ChatMessage chatMessageByBot = ChatMessageMapper.toDomain(
                        threadId,
                        replyJson,
                        SenderType.AI_BOT,
                        null,
                        orgCd
                );
                ChatMessageJpaEntity entityByBot = ChatMessageMapper.toEntity(chatMessageByBot);
                chatMessageRepository.save(entityByBot);
            }
        }catch (JsonProcessingException e) {
            log.error("참고문헌 데이터 변환 실패", e);
        }

        return aiResponse;
    }

    @Transactional
    public AiChatResponse testSend(String message, UUID threadId) {
        return aiClientAdapter.fetchChatResponse(new AiChatRequest(threadId, message));
    }

    private String sumMsg(String message) {
        if (message == null || message.isBlank()) {
            return "새로운 대화";
        }

        // 첫 번째 줄만 추출 (줄바꿈 제거)
        String firstLine = message.split("\\n")[0].trim();

        // 20자 제한 및 요약 처리
        if (firstLine.length() <= 20) {
            return firstLine;
        }

        return firstLine.substring(0, 20) + "...";
    }
}

