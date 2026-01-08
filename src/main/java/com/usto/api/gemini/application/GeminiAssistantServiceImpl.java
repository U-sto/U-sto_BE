package com.usto.api.gemini.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usto.api.common.exception.GeminiException;
import com.usto.api.gemini.domain.service.GeminiAssistantService;
import com.usto.api.gemini.infrastructure.GeminiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class GeminiAssistantServiceImpl
 * @desc Gemini AI 어시스턴트 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiAssistantServiceImpl implements GeminiAssistantService {
    
    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";
    
    @Override
    public String generateResponse(String prompt) {
        try {
            String url = String.format(GEMINI_API_URL, geminiConfig.getModel(), geminiConfig.getApiKey());
            
            // 요청 본문 구성
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));
            
            requestBody.put("contents", List.of(content));
            
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseResponse(response.getBody());
            } else {
                throw new GeminiException("Gemini API 호출 실패: " + response.getStatusCode());
            }
            
        } catch (GeminiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Gemini AI 응답 생성 실패: {}", e.getMessage(), e);
            throw new GeminiException("AI 응답 생성에 실패했습니다.", e);
        }
    }
    
    private String parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode content = firstCandidate.path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
            throw new GeminiException("응답 파싱 실패: 예상하지 못한 응답 형식");
        } catch (Exception e) {
            log.error("Gemini 응답 파싱 실패: {}", e.getMessage(), e);
            throw new GeminiException("응답 파싱에 실패했습니다.", e);
        }
    }
}
