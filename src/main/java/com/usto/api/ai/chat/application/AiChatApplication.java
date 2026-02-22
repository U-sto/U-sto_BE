package com.usto.api.ai.chat.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.chat.domain.model.ChatMessage;
import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.ai.chat.domain.model.SenderType;
import com.usto.api.ai.chat.domain.repository.ChatMessageRepository;
import com.usto.api.ai.chat.domain.repository.ChatThreadRepository;
import com.usto.api.ai.chat.domain.service.ChatThreadPolicy;
import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;
import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;
import com.usto.api.ai.chat.infrastructure.mapper.ChatMessageMapper;
import com.usto.api.ai.chat.infrastructure.mapper.ChatThreadMapper;
import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.ai.chat.presentation.dto.response.ChatMessageResponse;
import com.usto.api.ai.common.AiClientAdapter;
import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.disposal.domain.model.DisposalMaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatApplication {

    private final AiClientAdapter aiClientAdapter;
    private final ObjectMapper objectMapper;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatThreadRepository chatThreadRepository;
    private final ChatThreadPolicy chatThreadPolicy;


    @Transactional
    public AiChatResponse send(String userid,String orgCd, String message, UUID threadId) {

        if(threadId == null){
            String title = sumMsg(message);
            ChatThread master = ChatThreadMapper.toDomain(userid,title,orgCd);

            chatThreadRepository.save(master);

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
            String refJson = null;
            if (aiResponse.references() != null) {
                String replyJson = aiResponse.reply();
                refJson = objectMapper.writeValueAsString(aiResponse.references());

                ChatMessage chatMessageByBot = ChatMessageMapper.toDomain(
                        threadId,
                        replyJson,
                        SenderType.AI_BOT,
                        refJson,
                        orgCd
                );
                ChatMessageJpaEntity entityByBot = ChatMessageMapper.toEntity(chatMessageByBot);
                chatMessageRepository.save(entityByBot);
            }
        }catch (JsonProcessingException e) {
            log.error("참고문헌 데이터 변환 실패", e);
            throw new RuntimeException("AI 응답 데이터 처리 중 오류가 발생했습니다.", e);
        }

        return aiResponse;
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

    public List<UUID> threads(String username) {

        List<ChatThread> threads = chatThreadRepository.findByUsrId(username);

        List<UUID> result = new ArrayList<>();
        for (ChatThread thread : threads) {
            result.add(thread.getThreadId());
        }

        return result;
    }

    public void deleteThread(UUID threadId, String username) {

        ChatThread thread = chatThreadRepository.findById(threadId);
        if(thread == null){
            throw new BusinessException("존재하지 않는 채팅방입니다.");
        }
        chatThreadPolicy.validateOwnership(thread,username);
        chatThreadRepository.deleteThread(threadId);
    }

    public List<String> findContent(String content, String username) {

        List<String> messages = chatMessageRepository.findByContent(content,username);
        if(messages.isEmpty()){
            throw new BusinessException("존재하지 않는 대화기록입니다.");
        }
        return messages;
    }

    public List<ChatMessageResponse> findMessage(UUID threadId, String username) {

        ChatThread thread = chatThreadRepository.findById(threadId);
        if(thread == null){
            throw new BusinessException("존재하지 않는 채팅방입니다.");
        }
        chatThreadPolicy.validateOwnership(thread,username);

        List<ChatMessage> messages = chatMessageRepository.findByThreadIdOrderByCreAtAsc(threadId);

        List<ChatMessageResponse> result = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            ChatMessage message = messages.get(i);
            result.add(
                    new ChatMessageResponse(
                            i + 1,
                            message.getSender().name(),
                            message.getContent()
                    )
            );
        }

        return result;
    }
}

