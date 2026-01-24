package com.usto.api.g2b.application;

import com.usto.api.common.utils.ShoppingMallOpenApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class G2bTestServiceImpl {

    private final ShoppingMallOpenApiClient client;

    public ShoppingMallOpenApiClient.PageResult test(
            String pageNo,
            String numOfRows,
            String inqryDiv,
            String inqryBgnDate,
            String inqryEndDate
    ) {
        // 기본값 채우기
        String p = (pageNo == null || pageNo.isBlank()) ? "1" : pageNo.trim();
        String n = (numOfRows == null || numOfRows.isBlank()) ? "10" : numOfRows.trim();
        String d = (inqryDiv == null || inqryDiv.isBlank()) ? "1" : inqryDiv.trim();

        String b = normalizeYyyyMMdd(inqryBgnDate);
        String e = normalizeYyyyMMdd(inqryEndDate);

        // 선택: begin/end가 모두 있고, begin > end면 스왑
        if (b != null && e != null && b.compareTo(e) > 0) {
            String tmp = b; b = e; e = tmp;
        }

        return client.fetch(p, n, d, b, e);
    }

    private String normalizeYyyyMMdd(String v) {
        if (v == null) return null;
        String t = v.trim();
        if (t.length() != 8) return null;
        // 숫자 8자리만 허용
        for (int i = 0; i < 8; i++) {
            char c = t.charAt(i);
            if (c < '0' || c > '9') return null;
        }
        return t;
    }
}
