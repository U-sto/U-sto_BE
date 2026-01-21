package com.usto.api.g2b.domain.model;

import com.usto.api.g2b.presentation.dto.response.ShoppingMallEnvelope;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StgPriceRowMapper {

    public static List<StgPriceRow> toStgRows(List<ShoppingMallEnvelope.Item> items) {
        if (items == null || items.isEmpty()) return List.of();

        // last-wins 정책 (필요하면 최저가/최신가 정책으로 교체)
        Map<String, Long> map = new LinkedHashMap<>();
        for (var i : items) {
            String key = norm(i.prdctIdntNo());
            long price = parsePrice(i.cntrctPrceAmt());

            if (key == null || key.isBlank()) continue;
            if (price <= 0) continue;

            map.put(key, price);
        }

        return map.entrySet().stream()
                .map(e -> new StgPriceRow(e.getKey(), e.getValue()))
                .toList();
    }

    private static String norm(String v) { return v == null ? null : v.trim(); }

    private static long parsePrice(String v) {
        if (v == null) return -1L;
        String digits = v.replaceAll("[^0-9]", "");
        if (digits.isBlank()) return -1L;
        try {
            long x = Long.parseLong(digits);
            return x > 0 ? x : -1L;
        } catch (NumberFormatException e) {
            return -1L;
        }
    }
}