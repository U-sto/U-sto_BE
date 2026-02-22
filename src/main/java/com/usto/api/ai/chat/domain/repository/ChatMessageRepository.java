package com.usto.api.ai.chat.domain.repository;

import com.usto.api.ai.chat.domain.model.ChatMessage;
import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;

import java.util.List;
import java.util.UUID;


public interface ChatMessageRepository {

    void save(ChatMessageJpaEntity entity);

    List<String> findByContent(String content, String username);

    List<ChatMessage> findByThreadId(UUID threadId);
}
