package com.usto.api.ai.chat.application;

import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
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
        String aiReply = chatClient.prompt()
                .user(message)
                .call()
                .content();

        return AiChatResponse.builder()
                .replyMessage(aiReply)
                .build();
    }
}
