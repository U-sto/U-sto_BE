package com.usto.api.g2b.infrastructure.entity;

import com.usto.api.common.utils.ShoppingMallEnvelope;
import com.usto.api.g2b.domain.model.G2bStg;
import java.math.BigDecimal;

public class G2bStgMapper {



    public static G2bStg toDomain(G2bStgJpaEntity e) {
        if (e == null) return null;

        // 도메인에서 필수값 정책 유지(기존 Item 변환과 같은 기준으로)
        if (e.getG2bDCd() == null || e.getG2bDCd().isBlank()) return null;
        if (e.getG2bUpr() == null || e.getG2bUpr().signum() <= 0) return null;

        return G2bStg.builder()
                .g2bMCd(e.getG2bMCd())
                .g2bMNm(e.getG2bMNm())
                .g2bDCd(e.getG2bDCd())
                .g2bDNm(e.getG2bDNm())
                .g2bUpr(e.getG2bUpr().longValueExact()) // scale=0 이므로 안전(오버플로우 주의)
                .build();
    }

    //data -> domain
    public static G2bStg toDomain(ShoppingMallEnvelope.Item item) {
        if (item == null) return null;

        String mCd = norm(item.prdctClsfcNo());
        String mNm = norm(item.prdctClsfcNoNm());
        String dCd = norm(item.prdctIdntNo());
        String dNm = norm(item.prdctSpecNm());
        String uprStr = item.cntrctPrceAmt(); // 보통 문자열로 옴

        if (dCd == null) return null;
        if (uprStr == null || uprStr.isBlank()) return null;

        long upr;
        try {
            // 핵심 수정: 콤마(,) 제거 및 공백 제거
            upr = Long.parseLong(uprStr.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return null; // 또는 예외 던지기
        }
        if (upr <= 0) return null;

        return G2bStg.builder()
                .g2bMCd(mCd)
                .g2bMNm(mNm)
                .g2bDCd(dCd)
                .g2bDNm(dNm)
                .g2bUpr(upr)
                .build();
    }

    private static String norm(String s) {
        if (s == null || s.isBlank()) return null;
        return s.trim();
    }


    public static G2bStgJpaEntity toEntity(G2bStg domain) {
        if (domain == null) return null;
        return G2bStgJpaEntity.builder()
                .g2bMCd(domain.getG2bMCd())
                .g2bMNm(domain.getG2bMNm())
                .g2bDCd(domain.getG2bDCd())
                .g2bDNm(domain.getG2bDNm())
                .g2bUpr(BigDecimal.valueOf(domain.getG2bUpr()))
                .build();
    }
}