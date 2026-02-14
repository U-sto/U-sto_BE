package com.usto.api.ai.common;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class AiClientConfig {

    @Bean
    public WebClient aiWebClient(
            //@Value("${ai.base-url}") String baseUrl,
            @Value("${AI_API_KEY}") String apiKey,
            @Value("${ai.timeout-ms}") int timeoutMs
    ) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(timeoutMs));

        WebClient.Builder b = WebClient.builder()
                //.baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        if (apiKey != null && !apiKey.isBlank()) {
            b.defaultHeader("X-API-KEY", apiKey);
        }

        return b.build();
    }
}

