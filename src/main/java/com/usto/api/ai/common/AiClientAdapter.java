package com.usto.api.ai.common;

import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiClientAdapter {

    private final WebClient aiWebClient;
    private final AiProperties properties;

    public AiChatResponse fetchChatResponse(AiChatRequest request) {
        return aiWebClient.post()
                .uri(properties.endpoints().chat())
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("AI API 호출 실패: " + error)))
                )
                .bodyToMono(AiChatResponse.class)
                .block();
    }
}
