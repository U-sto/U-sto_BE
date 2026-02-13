package com.usto.api.ai.chat.infrastructure.mapper;

import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class ChatThreadMapper {
    public static ChatThread toDomain(String userId, String title, String orgCode) {
        return ChatThread.builder()
                .threadId(UUID.randomUUID())
                .userId(userId)
                .title(title) //자바로 구현해서 기본값 만들어야함
                .lastMessageAt(LocalDateTime.now())
                .orgCode(orgCode)
                .messages(new ArrayList<>())
                .build();
    }

    // Overload 2: Entity -> Domain (For reading from DB)
    public static ChatThread toDomain(ChatThreadJpaEntity entity) {
        return ChatThread.builder()
                .threadId(entity.getThreadId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .lastMessageAt(entity.getLastMessageAt())
                .orgCode(entity.getOrgCode())
                .build();
    }

    // Domain -> Entity
    public static ChatThreadJpaEntity toEntity(ChatThread domain) {
        return ChatThreadJpaEntity.builder()
                .threadId(domain.getThreadId())
                .userId(domain.getUserId())
                .title(domain.getTitle())
                .lastMessageAt(domain.getLastMessageAt())
                .orgCode(domain.getOrgCode())
                .build();
    }
}
