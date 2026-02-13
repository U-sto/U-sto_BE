package com.usto.api.ai.chat.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AiChatRequest(
        Long threadId,          // nullable
        @NotBlank  String message
) { }