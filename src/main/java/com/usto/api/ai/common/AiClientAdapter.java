package com.usto.api.ai.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.common.utils.ApiResponse;
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
    private final ObjectMapper objectMapper; // JSON 파싱을 위한 매퍼 추가

    public AiChatResponse fetchChatResponse(AiChatRequest request) {
        // 1. 먼저 String으로 응답을 받아서 로그를 확인
        String rawResponse =
                aiWebClient.post()
                .uri(properties.endpoints().chat())
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("AI API 호출 실패: " + error)))
                )
                .bodyToMono(String.class)
                .block();

        log.info("AI Server Raw Response: {}", rawResponse);

        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);

            // 2. "data" 키에 해당하는 자식 노드만 추출
            JsonNode dataNode = rootNode.get("data");

            if (dataNode != null && !dataNode.isNull()) {
                return objectMapper.treeToValue(dataNode, AiChatResponse.class);
            }
        } catch (Exception e) {
            log.error("JSON 매핑 실패: {}", e.getMessage());
            // 매핑 실패 시 빈 객체나 에러 처리를 진행합니다.
            return AiChatResponse.builder()
                    .replyMessage("데이터 매핑에 실패했습니다. 로그를 확인하세요.")
                    .build();
        }
        return null;
    }
}
