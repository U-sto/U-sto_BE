package com.usto.api.ai.chat.presentation.dto.response;

import lombok.Builder;

@Builder
public record AiChatResponse(
        String replyMessage
) {}
