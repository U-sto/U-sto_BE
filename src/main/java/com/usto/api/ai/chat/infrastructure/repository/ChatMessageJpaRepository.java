package com.usto.api.ai.chat.infrastructure.repository;
import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageJpaEntity, Long> {
    ChatMessageJpaEntity save(ChatMessageJpaEntity entity);
}
