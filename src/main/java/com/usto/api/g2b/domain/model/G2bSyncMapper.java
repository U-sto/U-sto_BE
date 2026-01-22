package com.usto.api.g2b.domain.model;

import com.usto.api.g2b.presentation.dto.response.ShoppingMallEnvelope;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class G2bSyncMapper {

    public static List<G2bSync> toG2bSync(List<ShoppingMallEnvelope.Item> items) {
        if (items == null || items.isEmpty()) return List.of();

        // last-wins 정책 (필요하면 최저가/최신가 정책으로 교체)
        Map<String, G2bSync> map = new LinkedHashMap<>();
        for (var i : items) {
            String mCd = norm(i.prdctClsfcNo());
            String mNm = norm(i.prdctClsfcNoNm());
            String dCd = norm(i.prdctIdntNo());
            String dNm = norm(i.prdctSpecNm());
            long upr    = parsePrice(i.cntrctPrceAmt());

            if (dCd == null || dCd.isBlank()) continue; // 필수
            if (upr <= 0) continue;                      // 0 또는 음수 가격 제외

            map.put(dCd, G2bSync.builder()
                    .g2bMCd(mCd)
                    .g2bMNm(mNm)
                    .g2bDCd(dCd)
                    .g2bDNm(dNm)
                    .g2bUpr(upr)
                    .build());
        }
        return List.copyOf(map.values());
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