package com.usto.api.ai.chat.presentation.dto.response;

public record ChatMessageResponse (
        Long messageId,
        String sender,
        String content
) {}
