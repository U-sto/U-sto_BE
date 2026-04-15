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
import com.usto.api.ai.chat.infrastructure.mapper.ChatMessageMapper;
import com.usto.api.ai.chat.infrastructure.mapper.ChatThreadMapper;
import com.usto.api.ai.chat.presentation.dto.request.AiChatToAiRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.ai.chat.presentation.dto.response.AiFirstChatResponse;
import com.usto.api.ai.chat.presentation.dto.response.ChatMessageResponse;
import com.usto.api.ai.common.AiChatAdapter;
import com.usto.api.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatApplication {

    private final AiChatAdapter aiClientAdapter;
    private final ObjectMapper objectMapper;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatThreadRepository chatThreadRepository;
    private final ChatThreadPolicy chatThreadPolicy;
    private final ChatGptApplication chatGptTestApplication;

    @Transactional
    public AiChatResponse send(String userId,String orgCd, String message, UUID threadId) {

        ChatThread thread = chatThreadRepository.findById(threadId);
        if (thread == null) {
            throw new BusinessException("존재하지 않는 채팅방입니다.");
        }
        chatThreadPolicy.validateOwnership(thread,userId);

        ChatMessage chatMessageByUser = ChatMessageMapper.toDomain(
                threadId,
                message,
                SenderType.USER,
                null,
                orgCd
        );
        ChatMessageJpaEntity entityByUser = ChatMessageMapper.toEntity(chatMessageByUser);
        chatMessageRepository.save(entityByUser);

        AiChatToAiRequest request = new AiChatToAiRequest(threadId, message);
        AiChatResponse aiResponse = aiClientAdapter.fetchChatResponse(request);

        if (aiResponse == null) {
            log.error("챗봇에게서 답장을 받지 못함. 저장 스킵 threadId: {}", threadId);
            throw new BusinessException("AI 응답을 받지 못했습니다.");
        }

        log.info("AI Response: {}", aiResponse);

        try {

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

        }catch (JsonProcessingException e) {
            log.error("참고문헌 데이터 변환 실패", e);
            throw new RuntimeException("AI 응답 데이터 처리 중 오류가 발생했습니다.", e);
        }

        return aiResponse;
    }

    @Transactional
    public AiFirstChatResponse sendAtFirst(String userId,String orgCd, String message) {

        //쓰레드 생성
        String title = makeTitle(message);
        ChatThread master = ChatThreadMapper.toDomain(userId,title,orgCd);
        chatThreadRepository.save(master);
        UUID threadId = master.getThreadId();


        ChatMessage chatMessageByUser = ChatMessageMapper.toDomain(
                threadId,
                message,
                SenderType.USER,
                null,
                orgCd
        );

        ChatMessageJpaEntity entityByUser = ChatMessageMapper.toEntity(chatMessageByUser);
        chatMessageRepository.save(entityByUser);

        AiChatToAiRequest request = new AiChatToAiRequest(threadId, message);
        AiFirstChatResponse.AiChatResponse aiResponse = aiClientAdapter.fetchChatResponseAtFirst(request);

        if (aiResponse == null) {
            log.error("챗봇에게서 답장을 받지 못함. 저장 스킵 threadId: {}", threadId);
            return null;
        }

        log.info("AI Response: {}", aiResponse);

        try {
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

        }catch (JsonProcessingException e) {
            log.error("참고문헌 데이터 변환 실패", e);
            throw new RuntimeException("AI 응답 데이터 처리 중 오류가 발생했습니다.", e);
        }

        AiFirstChatResponse firstChatResponse = new AiFirstChatResponse(threadId,aiResponse);

        return firstChatResponse;
    }


    public List<UUID> getThreads(String username) {

        List<ChatThread> threads = chatThreadRepository.findByUsrId(username);

        for (ChatThread thread : threads) {
            chatThreadPolicy.validateOwnership(thread,username);
        }

        if(threads.isEmpty()){
            throw new BusinessException("대화 기록이 없습니다.");
        }

        return threads.stream()
                .map(ChatThread::getThreadId)
                .toList();
    }

    public void deleteThread(UUID threadId, String username) {

        ChatThread thread = chatThreadRepository.findById(threadId);
        if(thread == null){
            throw new BusinessException("존재하지 않는 채팅방입니다.");
        }
        chatThreadPolicy.validateOwnership(thread,username);
        chatThreadRepository.deleteThread(threadId);
    }

    public void updateThread(UUID threadId, String newTitle,String username) {

        ChatThread thread = chatThreadRepository.findById(threadId);
        if(thread == null){
            throw new BusinessException("존재하지 않는 채팅방입니다.");
        }
        chatThreadPolicy.validateOwnership(thread,username);

        thread.updateTitle(newTitle);

        chatThreadRepository.save(thread);
    }

    public List<String> findContent(String content, String username) {

        List<String> messages = chatMessageRepository.findByContent(content,username);
        if(messages.isEmpty()){
            return null;
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

    private String makeTitle(String message) {

        if(message == null){
            throw new BusinessException("메시지 누락");
        }

        String instruction = """
        [Task] 메시지를 분석해 핵심 키워드 중심의 제목 후보 5개를 생성하세요.
        [Policy] 
        1. 20자 이내, 명사 위주 조합.
        2. 결과는 반드시 콤마(,)로 구분된 한 줄로 출력할 것.
        (예: 프로젝트 일정, 스프링 배포, 연동 가이드, 에러 로그, 자바 공부)
        
        메시지 내용: """ + message;

        String gptResponse = chatGptTestApplication.call(instruction);

        List<String> candidates = Arrays.stream(gptResponse.split(","))
                .map(String::trim)
                .toList();

        for (String candi : candidates) {
            if (!chatThreadRepository.existsByTitle(candi)) {
                return candi;
            }
        }

        //이래도 안된다? 그러면 최후의 수로 이렇게 한다.
        return candidates.get(0) + "_" + LocalTime.now().format(DateTimeFormatter.ofPattern("mmss"));
    }
}

