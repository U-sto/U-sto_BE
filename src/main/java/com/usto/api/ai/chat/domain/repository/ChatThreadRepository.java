package com.usto.api.ai.chat.domain.repository;

import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatThreadRepository {

    void save(ChatThread chatThread);

    List<ChatThread> findByUsrId(String username);

    ChatThread findById(UUID threadId);

    void deleteThread(UUID threadId);
}
