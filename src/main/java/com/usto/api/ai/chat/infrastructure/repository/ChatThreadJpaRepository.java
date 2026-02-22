package com.usto.api.ai.chat.infrastructure.repository;

import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatThreadJpaRepository extends JpaRepository<ChatThreadJpaEntity, UUID> {

    ChatThreadJpaEntity save(ChatThreadJpaEntity entity);

    List<ChatThreadJpaEntity> findAllByUserId(String username);

    ChatThreadJpaEntity findByThreadId(UUID threadId);

    void deleteById(UUID threadId);
}
