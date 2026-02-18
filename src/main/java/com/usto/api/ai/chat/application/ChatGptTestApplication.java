package com.usto.api.ai.chat.application;

import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.common.exception.BusinessException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatGptTestApplication {

    private final ChatClient chatClient;
    public ChatGptTestApplication(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Transactional
    public AiChatResponse testSend(String message) {

        if(message == null){
            throw new BusinessException("메시지 누락");
        }

        String aiReply = chatClient.prompt()
                .user(message)
                .call()
                .content();

        return AiChatResponse.builder()
                .reply(aiReply)
                .build();
    }
}
