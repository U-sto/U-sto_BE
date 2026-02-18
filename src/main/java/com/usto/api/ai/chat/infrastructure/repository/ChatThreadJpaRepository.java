package com.usto.api.ai.chat.infrastructure.repository;

import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ChatThreadJpaRepository extends JpaRepository<ChatThreadJpaEntity, UUID> {

    ChatThreadJpaEntity save(ChatThreadJpaEntity entity);


}
