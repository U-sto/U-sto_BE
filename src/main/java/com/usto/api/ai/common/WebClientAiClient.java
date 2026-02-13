package com.usto.api.ai.common;

import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.ai.chat.presentation.dto.response.VendorChatResponse;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResult;
import com.usto.api.ai.forecast.presentation.dto.response.VendorForecastResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WebClientAiClient implements AiClient {

    private final WebClient aiWebClient;

    @Value("${ai.endpoints.chat}")
    private String chatPath;

    @Value("${ai.endpoints.forecast}")
    private String forecastPath;

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        VendorChatResponse res = post(chatPath, request, VendorChatResponse.class);
        if (res == null || res.reply() == null || res.reply().isBlank()) {
            throw new AiCallException("AI_INVALID_RESPONSE", "챗봇 응답이 비었습니다.");
        }
        return new AiChatResponse(res.reply());
    }

    @Override
    public AiForecastResult forecast(AiForecastRequest request) {
        VendorForecastResponse res = post(forecastPath, request, VendorForecastResponse.class);
        if (res == null) throw new AiCallException("AI_INVALID_RESPONSE", "예측 응답이 비었습니다.");
        // 최소 필수값 검증(너희 정책대로)
        if (res.tsJson() == null || res.matrixJson() == null || res.recoJson() == null) {
            throw new AiCallException("AI_INVALID_RESPONSE", "예측 응답 필수 필드 누락");
        }
        return new AiForecastResult(res.tsJson(), res.matrixJson(), res.recoJson());
    }

    private <T> T post(String path, Object body, Class<T> responseType) {
        try {
            return aiWebClient.post()
                    .uri(path)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, r ->
                            r.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(raw -> Mono.error(new AiCallException(
                                            "AI_HTTP_ERROR",
                                            "AI 서버 오류(" + r.statusCode().value() + "): " + raw
                                    )))
                    )
                    .bodyToMono(responseType)
                    .block();
        } catch (AiCallException e) {
            throw e;
        } catch (Exception e) {
            throw new AiCallException("AI_CALL_FAILED", "AI 호출 실패", e);
        }
    }
}

