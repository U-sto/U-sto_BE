package com.usto.api.ai.chat.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ChatMessage {
    private final Long messageId;
    private final UUID threadId;
    private final String content;
    private final SenderType sender; // USER,
    private final String refer;
    private final String orgCode;
}
