package com.usto.api.ai.chat.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ChatThread {
    private final UUID threadId;
    private final String userId;
    private final String title;
    private final LocalDateTime lastMessageAt;
    private final String orgCode;
    private final List<ChatMessage> messages;
}
