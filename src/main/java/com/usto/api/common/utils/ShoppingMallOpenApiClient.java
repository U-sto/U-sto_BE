package com.usto.api.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@Component
public class ShoppingMallOpenApiClient {

    private final WebClient webClient;

    private String baseUrl = "http://apis.data.go.kr/1230000/at/ShoppingMallPrdctInfoService";
    private String path = "/getShoppingMallPrdctInfoList";


    @Value("${g2b.api.key}")
    private String serviceKey;

    public ShoppingMallOpenApiClient(WebClient.Builder builder) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();

        this.webClient = builder
                .exchangeStrategies(strategies)
                .build();
    }

    public PageResult fetch(
            String pageNo,
            String numberOfRows,
            String inqryDiv
    )
    {
        return fetch(pageNo, numberOfRows, inqryDiv, null, null);
    }

    public PageResult fetch(
            String pageNo,
            String numberOfRows,
            String inqryDiv,
            String begin,
            String end
            ) {

        var b = UriComponentsBuilder
                .fromHttpUrl(baseUrl + path)
                // 문서 기준으로 ServiceKey인지 serviceKey인지 여기만 맞추면 됨
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numberOfRows)
                .queryParam("inqryDiv", inqryDiv)
                .queryParam("type", "json");

        if (begin != null && !begin.isBlank()) b.queryParam("inqryBgnDate", begin);
        if (end != null && !end.isBlank()) b.queryParam("inqryEndDate", end);

        URI uri = b.build(true).toUri();

        ShoppingMallEnvelope env = webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(ShoppingMallEnvelope.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofMillis(500))
                                .maxBackoff(Duration.ofSeconds(5))
                                .filter(ex ->
                                        ex instanceof org.springframework.web.reactive.function.client.WebClientResponseException w &&
                                                (w.getStatusCode().is5xxServerError() || w.getStatusCode().value() == 504)
                                )
                )
                .block(Duration.ofSeconds(10));

        var resp = (env == null) ? null : env.response();
        var header = (resp == null) ? null : resp.header();
        var body = (resp == null) ? null : resp.body();

        if (header == null || !"00".equals(header.resultCode())) {
            String code = (header == null) ? "NO_HEADER" : header.resultCode();
            String msg = (header == null) ? "no header" : header.resultMsg();
            throw new IllegalStateException("G2B API error: code=" + code + ", msg=" + msg);
        }
        if (body == null) return new PageResult(0, List.of());

        int total = parseInt(body.totalCount());
        List<ShoppingMallEnvelope.Item> items = (body.items() == null) ? List.of() : body.items();
        return new PageResult(total, items);
    }

    private int parseInt(String v) {
        try { return Integer.parseInt(v); } catch (Exception e) { return 0; }
    }

    public record PageResult(int totalCount, List<ShoppingMallEnvelope.Item> items) {}
}
