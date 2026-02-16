package com.usto.api.ai.chat.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AiChatRequest(
        UUID threadId,          // nullable
        @NotBlank  String message
) { }