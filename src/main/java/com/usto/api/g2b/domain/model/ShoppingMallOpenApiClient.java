package com.usto.api.g2b.domain.model;

import com.usto.api.g2b.presentation.dto.response.ShoppingMallEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static java.lang.Integer.parseInt;

@Component
@RequiredArgsConstructor
public class ShoppingMallOpenApiClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${g2b.api.url}")
    private String baseUrl;

    private String path = "/getShoppingMallPrdctInfoList";

    @Value("${g2b.api.key}")
    private String serviceKey;

    public PageResult fetch(
            String pageNo,
            String numberOfRows,
            String inqryDiv,
            String inqryBgnDate,
            String inqryEndDate
            ) {

        var b = UriComponentsBuilder
                .fromHttpUrl(baseUrl + path)
                // 문서 기준으로 ServiceKey인지 serviceKey인지 여기만 맞추면 됨
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numberOfRows)
                .queryParam("inqryDiv", inqryDiv)
                .queryParam("type", "json");

        //빈값 체크 (혹시나 빠지면 로직 중단)
        if (inqryBgnDate != null && !inqryBgnDate.isBlank()) b.queryParam("inqryBgnDate", inqryBgnDate);
        if (inqryEndDate != null && !inqryEndDate.isBlank()) b.queryParam("inqryEndDate", inqryEndDate);

        URI uri = b.build(true).toUri();

        ShoppingMallEnvelope env = webClientBuilder.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(ShoppingMallEnvelope.class)
                .block();

        var body = (env == null || env.response() == null) ? null : env.response().body();
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
