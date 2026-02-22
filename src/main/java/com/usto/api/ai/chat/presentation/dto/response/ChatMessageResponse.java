package com.usto.api.ai.chat.presentation.dto.response;

public record ChatMessageResponse (
        int order,
        String sender,
        String content
) {}
