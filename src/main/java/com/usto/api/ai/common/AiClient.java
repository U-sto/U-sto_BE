package com.usto.api.ai.common;

import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResult;

public interface AiClient {
    AiChatResponse chat(AiChatRequest request);
    AiForecastResult forecast(AiForecastRequest request);
}
