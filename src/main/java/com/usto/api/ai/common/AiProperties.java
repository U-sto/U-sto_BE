package com.usto.api.ai.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "ai.server")
public record AiProperties(
        String baseUrl,
        String apiKey,
        Endpoints endpoints,
        Timeout timeout
) {
    public record Endpoints(String chat, String forecast) {}
    public record Timeout(Duration chat, Duration forecast) {}
}
