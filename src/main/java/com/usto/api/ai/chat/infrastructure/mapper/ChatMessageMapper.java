package com.usto.api.ai.chat.infrastructure.mapper;

import com.usto.api.ai.chat.domain.model.ChatMessage;
import com.usto.api.ai.chat.domain.model.SenderType;
import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;

import java.util.UUID;

public class ChatMessageMapper {

    //data -> domain
    public static ChatMessage toDomain(UUID threadId, String content, SenderType sender, String orgCode) {
        return ChatMessage.builder()
                .threadId(threadId)
                .content(content)
                .sender(sender)
                .orgCode(orgCode)
                .build();
    }

    // Overload : Entity -> Domain (For reading from DB)
    public static ChatMessage toDomain(ChatMessageJpaEntity entity) {
        return ChatMessage.builder()
                .massageId(entity.getMassageId())
                .threadId(entity.getThreadId())
                .content(entity.getContent())
                .sender(entity.getSender())
                .orgCode(entity.getOrgCode())
                .build();
    }

    // Domain -> Entity
    public static ChatMessageJpaEntity toEntity(ChatMessage domain) {
        return ChatMessageJpaEntity.builder()
                .threadId(domain.getThreadId())
                .content(domain.getContent())
                .sender(domain.getSender())
                .orgCode(domain.getOrgCode())
                .build();
    }
}
