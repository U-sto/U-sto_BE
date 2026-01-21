package com.usto.api.g2b.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.usto.api.common.utils.ItemsFlexibleDeserializer;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShoppingMallEnvelope(Response response) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(Header header, Body body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Header(String resultCode, String resultMsg) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(
            @JsonDeserialize(using = ItemsFlexibleDeserializer.class)
            List<Item> items,
            String numOfRows,
            String pageNo,
            String totalCount
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String prdctClsfcNo,     // 물품분류번호
            String prdctClsfcNoNm,   // 물품분류명
            String prdctIdntNo,      // 물품식별번호
            String prdctSpecNm,      // 물품식별명
            String cntrctPrceAmt     // 계약단가
    ) {}
}
