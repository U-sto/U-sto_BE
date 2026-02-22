package com.usto.api.ai.chat.domain.repository;

import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;

import java.util.List;


public interface ChatMessageRepository {

    void save(ChatMessageJpaEntity entity);

    List<String> findByContent(String content, String username);
}
