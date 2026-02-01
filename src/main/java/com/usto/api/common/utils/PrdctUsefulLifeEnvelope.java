package com.usto.api.common.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PrdctUsefulLifeEnvelope(Response response) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(Header header, Body body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Header(String resultCode, String resultMsg) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(
            @JsonDeserialize(using = UsefulLifeItemsDeserializer.class)
            List<Item> items,
            String numOfRows,
            String pageNo,
            String totalCount
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String prdctClsfcNo,     // 물품분류번호
            String prdctClsfcNoNm,   // 품명
            String uslfsvc            // 내용연수
    ) {}
}
