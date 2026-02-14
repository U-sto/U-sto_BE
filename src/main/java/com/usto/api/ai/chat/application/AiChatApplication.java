package com.usto.api.ai.chat.application;

import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.ai.openai.api.OpenAiApi.*;

@Service
public class AiChatApplication {

    //private final ChatThreadRepository threadRepository;
    //private final ChatMessageRepository messageRepository;
    //private final AiClient aiClient;

    private final ChatClient chatClient;
    public AiChatApplication(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Transactional
    public AiChatResponse send(String username, String message, UUID threadId) {

        /*
        // 1. Thread 조회 또는 생성
        ChatThread thread = (threadId == null)
                ? createNewThread(username, message)
                : validateAndGetThread(username, threadId);

        // 2. 사용자 메시지 저장
        saveMessage(thread, "USER", message);

*/
        // 3. AI 클라이언트 호출
        String aiReply = chatClient.prompt()
                .user(message)
                .call()
                .content();

        /*
        // 4. AI 응답 저장
        saveMessage(thread, "BOT", aiResponse.replyMessage());
*/
        return AiChatResponse.builder()
                .replyMessage(aiReply)
                .build();
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
}

