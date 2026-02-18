package com.usto.api.ai.chat.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AiChatRequest(

        @JsonProperty("session_id")
        UUID threadId,

        @NotBlank
        @JsonProperty("query")
        String message
) { }