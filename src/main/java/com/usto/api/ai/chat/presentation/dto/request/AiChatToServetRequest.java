package com.usto.api.ai.chat.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record AiChatToServetRequest(
        @NotBlank
        @JsonProperty("query")
        String message
) { }