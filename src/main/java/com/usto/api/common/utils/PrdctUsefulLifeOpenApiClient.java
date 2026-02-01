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
public class PrdctUsefulLifeOpenApiClient {

    private final WebClient webClient;

    // 요구사항 기준
    private final String baseUrl = "https://apis.data.go.kr/1230000/ao/PrdctMngInfoService";
    private final String path = "/getPrdctClsfcNoUslfsvc";

    @Value("${g2b.api.key}")
    private String serviceKey;

    public PrdctUsefulLifeOpenApiClient(WebClient.Builder builder) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(1024 * 1024 * 1024))
                .build();

        this.webClient = builder.exchangeStrategies(strategies).build();
    }

    public PageResult fetchByRange(String prdctClsfcNoBgnNo, String prdctClsfcNoEndNo) {
        return fetchByRange("1", "10", prdctClsfcNoBgnNo, prdctClsfcNoEndNo);
    }

    public PageResult fetchByRange(
            String pageNo,
            String numOfRows,
            String prdctClsfcNoBgnNo,
            String prdctClsfcNoEndNo
    ) {
        var b = UriComponentsBuilder
                .fromHttpUrl(baseUrl + path)
                // 문서 표기는 serviceKey (대소문자 이슈 있으면 여기만 변경)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("type", "json");

        if (prdctClsfcNoBgnNo != null && !prdctClsfcNoBgnNo.isBlank()) {
            b.queryParam("prdctClsfcNoBgnNo", prdctClsfcNoBgnNo);
        }
        if (prdctClsfcNoEndNo != null && !prdctClsfcNoEndNo.isBlank()) {
            b.queryParam("prdctClsfcNoEndNo", prdctClsfcNoEndNo);
        }

        URI uri = b.build(true).toUri();

        PrdctUsefulLifeEnvelope env = webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(PrdctUsefulLifeEnvelope.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofMillis(500))
                                .maxBackoff(Duration.ofSeconds(5))
                                .filter(ex ->
                                        ex instanceof org.springframework.web.reactive.function.client.WebClientResponseException w &&
                                                (w.getStatusCode().is5xxServerError() || w.getStatusCode().value() == 504)
                                )
                )
                .block(Duration.ofSeconds(600));

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
        List<PrdctUsefulLifeEnvelope.Item> items =
                (body.items() == null) ? List.of() : body.items();

        return new PageResult(total, items);
    }

    private int parseInt(String v) {
        try { return Integer.parseInt(v); } catch (Exception e) { return 0; }
    }

    public record PageResult(int totalCount, List<PrdctUsefulLifeEnvelope.Item> items) {}
}
