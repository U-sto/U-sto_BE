package com.usto.api.common.utils;

import org.springframework.stereotype.Component;

@Component
public class StringSimilarityUtils {

    /**
     * 문자열 전처리 (Normalization)
     * - 괄호 안의 내용 제거 (예: "(주)삼성" -> "삼성")
     * - 특수문자 제거
     * - 모든 공백 제거
     * - 영문 소문자 변환
     */
    public String normalize(String input) {
        if (input == null) return "";

        return input.replaceAll("\\(.*?\\)", "")  // 소괄호 내용 제거
                .replaceAll("\\[.*?\\]", "")  // 대괄호 내용 제거
                .replaceAll("[^a-zA-Z0-9가-힣]", "") // 특수문자 제거 (한글,영문,숫자만 남김)
                .toLowerCase(); // 소문자화
    }

    /**
     * 두 문자열 사이의 유사도 점수 계산 (0.0 ~ 1.0)
     * - 1.0: 완전히 동일함
     * - 0.0: 완전히 다름
     */
    public double calculateSimilarity(String s1, String s2) {
        String str1 = normalize(s1);
        String str2 = normalize(s2);

        // 둘 다 비어있으면 같다고 간주
        if (str1.isEmpty() && str2.isEmpty()) return 1.0;
        // 둘 중 하나만 비어있으면 다름
        if (str1.isEmpty() || str2.isEmpty()) return 0.0;
        // 전처리 후 문자열이 같으면 1.0
        if (str1.equals(str2)) return 1.0;

        int distance = calculateDistance(str1, str2);
        int maxLength = Math.max(str1.length(), str2.length());

        // 유사도 = 1 - (편집거리 / 최대길이)
        return 1.0 - ((double) distance / maxLength);
    }

    /**
     * Levenshtein Distance (편집 거리) 알고리즘
     * - 두 문자열을 같게 만들기 위해 필요한 최소 편집 횟수 계산
     */
    private int calculateDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];

        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            costs[0] = i;
            int nw = i - 1;

            for (int j = 1; j <= s2.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        s1.charAt(i - 1) == s2.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }

        return costs[s2.length()];
    }
}
