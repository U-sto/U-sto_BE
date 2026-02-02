package com.usto.api.common.utils;

import com.usto.api.g2b.domain.model.G2bItemCategory;
import com.usto.api.g2b.domain.model.G2bUsrfulList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class G2bUsefulLifeMatcher {

    private final StringSimilarityUtils similarityUtils;

    /**
     * 계층적 유사도 + 통계적 추론 기반 '무조건 채우기' 로직
     * 목표: 미매칭 0건 만들기
     */
    @Transactional
    public int matchBasedOnHierarchy(List<G2bUsrfulList> sources, List<G2bItemCategory> targets) {

        log.info("매칭 시작: Source {}건, Target {}건", sources.size(), targets.size());

        // 0. 성능 최적화를 위한 그룹핑
        // 8자리 일치
        Map<String, G2bUsrfulList> exactMatchMap = sources.stream()
                .collect(Collectors.toMap(
                        i -> i.getG2bMcd().trim(),
                        Function.identity(),
                        (e, r) -> e
                ));

        // [유사도용] 그룹별 그룹핑
        Map<String, List<G2bUsrfulList>> smallClassMap = sources.stream()
                .filter(i -> i.getG2bMcd().length() >= 6)//(가) 6자리일치
                .collect(Collectors.groupingBy(i -> i.getG2bMcd().substring(0, 6)));

        Map<String, List<G2bUsrfulList>> mediumClassMap = sources.stream()
                .filter(i -> i.getG2bMcd().length() >= 4)//(나) 4자리 일치
                .collect(Collectors.groupingBy(i -> i.getG2bMcd().substring(0, 4)));

        Map<String, List<G2bUsrfulList>> largeClassMap = sources.stream()
                .filter(i -> i.getG2bMcd().length() >= 2)//(다) 2자리 일치
                .collect(Collectors.groupingBy(i -> i.getG2bMcd().substring(0, 2)));

        int matchCount = 0;

        ListIterator<G2bItemCategory> iterator = targets.listIterator();

        while (iterator.hasNext()) {
            G2bItemCategory target = iterator.next();

            String rawCode = target.getG2bMCd();
            if (rawCode == null || rawCode.length() < 8) continue;
            String code = rawCode.trim();
            String targetName = target.getG2bMNm();

            String foundDrbYr = null;

            // 1. 코드 완전 일치
            if (exactMatchMap.containsKey(code)) {
                foundDrbYr = exactMatchMap.get(code).getDrbYr();
            } else {
                // 2. 유사도 & 통계 추론
                MatchResult bestResult = new MatchResult(null, -1.0);
                updateBestMatch(bestResult, targetName, smallClassMap.get(code.substring(0, 6)));

                if (bestResult.score < 0.9) updateBestMatch(bestResult, targetName, mediumClassMap.get(code.substring(0, 4)));
                if (bestResult.score < 0.8) updateBestMatch(bestResult, targetName, largeClassMap.get(code.substring(0, 2)));

                if (bestResult.item != null && bestResult.score > 0.3) {
                    foundDrbYr = bestResult.item.getDrbYr();
                } else {
                    // 통계적 추론 - 단순하게 전체 종류 비율을 기준으로 계산 -> 거의 사용하지 않음
                    foundDrbYr = findMostFrequentDrbYr(code, smallClassMap, mediumClassMap, largeClassMap);
                    if (foundDrbYr == null) foundDrbYr = "9"; // 최후의 보루
                }
            }

            // [교체 로직] 찾은 값이 있다면, 리스트의 요소를 새 객체로 교체(swap)
            if (foundDrbYr != null) {
                // target.updateDrbYr()는 새로운 객체를 반환함
                G2bItemCategory newObject = target.updateDrbYr(foundDrbYr);

                // 리스트의 현재 위치 값을 새 객체로 변경
                iterator.set(newObject);
                matchCount++;
            }
        }

        log.info("총 {}건 매칭 완료 및 객체 교체 수행", matchCount);
        return matchCount;
    }

    private void updateBestMatch(MatchResult currentBest, String targetName, List<G2bUsrfulList> candidates) {
        if (candidates == null || candidates.isEmpty()) return;
        String normalizedTarget = similarityUtils.normalize(targetName);
        for (G2bUsrfulList item : candidates) {
            String normalizedSource = similarityUtils.normalize(item.getG2bMNm());
            double score = 0.0;
            if (normalizedSource.contains(normalizedTarget) || normalizedTarget.contains(normalizedSource)) {
                score = 0.7 + (similarityUtils.calculateSimilarity(targetName, item.getG2bMNm()) * 0.3);
            } else {
                score = similarityUtils.calculateSimilarity(targetName, item.getG2bMNm());
            }
            if (score > currentBest.score) {
                currentBest.score = score;
                currentBest.item = item;
            }
        }
    }

    private String findMostFrequentDrbYr(String code, Map<String, List<G2bUsrfulList>> s, Map<String, List<G2bUsrfulList>> m, Map<String, List<G2bUsrfulList>> l) {
        List<G2bUsrfulList> list = s.get(code.substring(0, 6));
        if (list == null || list.isEmpty()) list = m.get(code.substring(0, 4));
        if (list == null || list.isEmpty()) list = l.get(code.substring(0, 2));
        if (list == null || list.isEmpty()) return null;

        Map<String, Integer> freq = new HashMap<>();
        for (G2bUsrfulList item : list) {
            String yr = item.getDrbYr();
            if (yr != null && !yr.isBlank()) freq.put(yr, freq.getOrDefault(yr, 0) + 1);
        }
        return freq.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
    }

    private static class MatchResult {
        G2bUsrfulList item;
        double score;
        public MatchResult(G2bUsrfulList item, double score) { this.item = item; this.score = score; }
    }
}
