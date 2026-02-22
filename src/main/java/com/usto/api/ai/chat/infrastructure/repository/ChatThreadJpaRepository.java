package com.usto.api.ai.chat.infrastructure.repository;

import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ChatThreadJpaRepository extends JpaRepository<ChatThreadJpaEntity, UUID> {

    ChatThreadJpaEntity save(ChatThreadJpaEntity entity);

    List<ChatThreadJpaEntity> findAllByUserId(String username);

    ChatThreadJpaEntity findByThreadId(UUID threadId);

    void deleteById(UUID threadId);
}
