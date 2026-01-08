package com.usto.api.gemini.infrastructure;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @class GeminiConfig
 * @desc Gemini AI API 설정 클래스
 */
@Configuration
@Getter
public class GeminiConfig {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    @Value("${gemini.api.model}")
    private String model;
    
    @Bean(name = "geminiRestTemplate")
    public RestTemplate geminiRestTemplate() {
        return new RestTemplate();
    }
}
