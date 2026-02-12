package com.usto.api.ai.chat.presentation.dto.request;

import java.util.List;

public record AiChatRequest(
        String message,
        List<History> history // 없으면 null/empty 허용
) {
    public record History(String senderType, String content) {} // USER/BOT
}