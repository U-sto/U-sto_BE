package com.usto.api.g2b.application;

import com.usto.api.g2b.domain.service.G2bSearchService;
import com.usto.api.g2b.infrastructure.entity.G2bItemCategoryJpaEntity;
import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;
import com.usto.api.g2b.infrastructure.repository.G2bItemCategoryJpaRepository;
import com.usto.api.g2b.infrastructure.repository.G2bItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.usto.api.common.exception.G2bBusinessException;
import java.util.Collections;
import java.util.List;

/**
 * @class G2bSearchServiceImpl
 * @desc G2B 검색 비즈니스 로직 구현 클래스
 */
@Service
@RequiredArgsConstructor
public class G2bSearchServiceImpl implements G2bSearchService {
    private final G2bItemCategoryJpaRepository categoryRepository;
    private final G2bItemJpaRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<G2bItemCategoryJpaEntity> findCategoryList(String code, String name) {
        // null 체크 후 빈 문자열로 치환하여 LIKE '%%'가 모든 값을 포함하도록 유도
        String searchCode = (code == null) ? "" : code.trim();
        String searchName = (name == null) ? "" : name.trim();

        // 검색어 길이 검증 (코드 및 명칭 검색 시 2글자 미만 제한)
        if ((!searchCode.isEmpty() && searchCode.length() < 2) ||
                (!searchName.isEmpty() && searchName.length() < 2)) {
            throw new G2bBusinessException("최소 2자 이상 입력해 주세요.");
        }

        return categoryRepository.findByFilters(searchCode, searchName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<G2bItemJpaEntity> findItemList(String mCd, String dCd, String dNm) {
        // null 체크 및 공백 제거 처리
        String categoryCode = (mCd == null) ? "" : mCd.trim();
        String itemCode = (dCd == null) ? "" : dCd.trim();
        String itemName = (dNm == null) ? "" : dNm.trim();

        // 검색어 길이 검증 (코드 및 명칭 검색 시 2글자 미만 제한)
        if ((!itemCode.isEmpty() && itemCode.length() < 2) ||
                (!itemName.isEmpty() && itemName.length() < 2)) {
            throw new G2bBusinessException("최소 2자 이상 입력해 주세요.");
        }

        // [방어 코드] 아무런 조건도 입력되지 않았을 경우, 전체 조회를 막아 DB 부하 방지
        if (categoryCode.isEmpty() && itemCode.isEmpty() && itemName.isEmpty()) {
            return Collections.emptyList();
        }

        // 동적 쿼리 호출 (mCd가 비어있어도 dCd나 dNm이 있다면 조회 성공)
        return itemRepository.findByFilters(categoryCode, itemCode, itemName);
    }
}