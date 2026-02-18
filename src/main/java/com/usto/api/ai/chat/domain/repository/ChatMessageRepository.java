package com.usto.api.ai.chat.domain.repository;

import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;


public interface ChatMessageRepository {

    void save(ChatMessageJpaEntity entity);

}
