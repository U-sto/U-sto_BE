package com.usto.api.ai.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.common.exception.BusinessException;
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
                        // 1. 서버 에러(5xx) 처리 - ngrok 장애 등 외부 API 불능 상태 대응
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .flatMap(errorBody -> {
                                            log.error("AI API 서버 오류 (5xx): {}", errorBody);
                                            return Mono.error(
                                                    new BusinessException("AI 서비스 일시 중단 (서버 점검 중)")
                                            );
                                        })
                        )
                        // 2. 클라이언트 에러(4xx) 처리
                        .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .flatMap(errorBody -> {
                                            log.error("AI API 요청 오류 (4xx): {}", errorBody);
                                            return Mono.error(
                                                    new BusinessException("AI 요청 형식이 올바르지 않습니다.")
                                            );
                                        })
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
            throw new RuntimeException("AI 서버 응답 파싱에 실패했습니다.", e);
        }
        throw new RuntimeException("AI 서버 응답에서 'data' 필드를 찾을 수 없습니다.");
    }
}
