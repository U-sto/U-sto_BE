package com.usto.api.item.common.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 물품고유번호 생성기
 * 규칙: M + 연도(4자리) + 순번(5자리) = 총 10자리
 * 예: M202600001
 */
@Component
public class ItemNumberGenerator {

    /**
     * 물품고유번호 생성
     * @param year 연도 (4자리)
     * @param sequence 순번 (1부터 시작)
     * @return M + 연도 + 순번 (예: M202600001)
     */
    public String generate(int year, int sequence) {
        return String.format("M%04d%05d", year, sequence);
    }


    /**
     * 현재 연도 기준 물품고유번호 생성
     */
    public String generateWithCurrentYear(int sequence) {
        int currentYear = LocalDate.now().getYear();
        return generate(currentYear, sequence);
    }

    /**
     * 물품고유번호에서 연도 추출
     */
    public int extractYear(String itemNo) {
        if (itemNo == null || itemNo.length() != 10 || !itemNo.startsWith("M")) {
            throw new IllegalArgumentException("유효하지 않은 물품고유번호 형식입니다: " + itemNo);
        }
        return Integer.parseInt(itemNo.substring(1, 5));
    }

    /**
     * 물품고유번호에서 순번 추출
     */
    public int extractSequence(String itemNo) {
        if (itemNo == null || itemNo.length() != 10 || !itemNo.startsWith("M")) {
            throw new IllegalArgumentException("유효하지 않은 물품고유번호 형식입니다: " + itemNo);
        }
        return Integer.parseInt(itemNo.substring(5, 10));
    }
}