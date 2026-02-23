package com.usto.api.ai.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.ai.forecast.presentation.dto.request.AiForecastRequest;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResponse;
import com.usto.api.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiForecastAdapter {

    private final WebClient aiWebClient;
    private final AiProperties properties;
    private final ObjectMapper objectMapper; // JSON 파싱을 위한 매퍼 추가

    public AiForecastResponse fetchForecastResponse(AiForecastRequest request) {
        // 1. 먼저 String으로 응답을 받아서 로그를 확인
        String rawResponse =
                aiWebClient.post()
                .uri(properties.endpoints().forecast())
                .bodyValue(request)
                .retrieve()
                        // 1. 서버 에러(5xx) 처리 - ngrok 장애 등 외부 API 불능 상태 대응
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .defaultIfEmpty("")
                                        .flatMap(errorBody -> {
                                            int code = clientResponse.statusCode().value();
                                            log.error("AI API 5xx 응답. status={}, body={}", code, errorBody);

                                            if (code == 502) {
                                                return Mono.error(new BusinessException("AI 게이트웨이 오류(502). 프록시/ngrok 또는 업스트림 장애 가능성이 있습니다."));
                                            }
                                            if (code == 503) {
                                                return Mono.error(new BusinessException("AI 서비스 이용 불가(503). 서버가 내려가 있거나 과부하 상태입니다."));
                                            }
                                            if (code == 504) {
                                                return Mono.error(new BusinessException("AI 응답 지연(504). 요청 처리 시간이 초과되었습니다."));
                                            }

                                            return Mono.error(new BusinessException("AI 서버 오류(5xx). 잠시 후 다시 시도해주세요."));
                                        })
                        )
                        // 2. 클라이언트 에러(4xx) 처리
                        .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .defaultIfEmpty("")
                                        .flatMap(errorBody -> {
                                            HttpStatusCode status = clientResponse.statusCode();

                                            // 무조건 남겨야 원인 파악 가능
                                            log.error("AI API 4xx 응답. status={}, body={}", status, errorBody);

                                            // 1) ngrok/offline HTML 감지
                                            if (looksLikeNgrokOfflinePage(errorBody)) {
                                                return Mono.error(new BusinessException(
                                                        "AI 서버 엔드포인트(ngrok)가 오프라인입니다. AI팀에 서버/터널 상태를 확인해주세요."
                                                ));
                                            }

                                            // 2) 상태코드별 의미 있는 메시지
                                            if (status.value() == HttpStatus.BAD_REQUEST.value()) {
                                                return Mono.error(new BusinessException("AI 요청이 올바르지 않습니다(400). 요청 스키마/필드를 확인해주세요. body=" + abbreviate(errorBody)));
                                            }
                                            if (status.value() == HttpStatus.UNAUTHORIZED.value()) {
                                                return Mono.error(new BusinessException("AI 인증 실패(401). API Key/토큰 설정을 확인해주세요."));
                                            }
                                            if (status.value() == HttpStatus.FORBIDDEN.value()) {
                                                return Mono.error(new BusinessException("AI 접근 거부(403). 권한/허용 IP/토큰 범위를 확인해주세요."));
                                            }
                                            if (status.value() == HttpStatus.NOT_FOUND.value()) {
                                                return Mono.error(new BusinessException("AI 엔드포인트를 찾을 수 없습니다(404). URL/path를 확인해주세요."));
                                            }
                                            if (status.value() == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                                                return Mono.error(new BusinessException("AI 요청 값 검증 실패(422). 필드 타입/enum 값을 확인해주세요. body=" + abbreviate(errorBody)));
                                            }
                                            if (status.value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                                                return Mono.error(new BusinessException("AI 요청이 너무 많습니다(429). 잠시 후 다시 시도해주세요."));
                                            }

                                            // 3) 기타 4xx: 바디 일부만 노출(너무 길면 축약)
                                            return Mono.error(new BusinessException("AI 요청 실패(" + status.value() + "). body=" + abbreviate(errorBody)));
                                        })
                        )
                .bodyToMono(String.class)
                .block();

        log.info("AI Server Raw Response: {}", rawResponse);

        if (rawResponse == null || rawResponse.isBlank()) {
            throw new BusinessException("AI 서버 응답이 비어있습니다.");
        }

        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);

            // 2. "data" 키에 해당하는 자식 노드만 추출
            JsonNode dataNode = rootNode.get("data");

            if (dataNode != null && !dataNode.isNull()) {
                return objectMapper.treeToValue(dataNode, AiForecastResponse.class);
            }
        } catch (Exception e) {
            log.error("JSON 매핑 실패: {}", e.getMessage());
            // 매핑 실패 시 빈 객체나 에러 처리를 진행합니다.
            throw new RuntimeException("AI 서버 응답 파싱에 실패했습니다.", e);
        }
        throw new RuntimeException("AI 서버 응답에서 'data' 필드를 찾을 수 없습니다.");
    }

    private static boolean looksLikeNgrokOfflinePage(String body) {
        if (body == null) return false;
        String b = body.toLowerCase();
        return b.contains("err_ngrok_3200")
                || b.contains("ngrok")
                || b.contains("<!doctype html")
                || b.contains("ngrok-free.dev is offline")
                || (b.contains("the endpoint") && b.contains("is offline"));
    }

    private static String abbreviate(String s) {
        if (s == null) return "";
        int max = 500;
        return s.length() <= max ? s : s.substring(0, max) + "...(truncated)";
    }
}
