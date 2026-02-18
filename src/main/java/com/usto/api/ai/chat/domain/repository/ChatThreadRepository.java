package com.usto.api.ai.chat.domain.repository;

import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;

public interface ChatThreadRepository {

    void save(ChatThreadJpaEntity masterEntity);
}
