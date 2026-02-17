package com.usto.api.g2b.application;

import com.usto.api.g2b.domain.repository.G2bSearchRepository;
import com.usto.api.g2b.infrastructure.entity.G2bItemCategoryJpaEntity;
import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;
import com.usto.api.g2b.infrastructure.repository.G2bItemCategoryJpaRepository;
import com.usto.api.g2b.infrastructure.repository.G2bItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.usto.api.common.exception.G2bBusinessException;

/**
 * @class G2bSearchServiceImpl
 * @desc G2B 검색 비즈니스 로직 구현 클래스
 */
@Service
@RequiredArgsConstructor
public class G2bSearchApplication implements G2bSearchRepository {
    private final G2bItemCategoryJpaRepository categoryRepository;
    private final G2bItemJpaRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<G2bItemCategoryJpaEntity> findCategoryList(String code, String name, Pageable pageable) {
        // null 체크 후 빈 문자열로 치환하여 LIKE '%%'가 모든 값을 포함하도록 유도
        String categoryCode = (code == null) ? "" : code.trim();
        String categoryName = (name == null) ? "" : name.trim();

        // 분류코드 검증: 입력했다면 숫자여야 함
        if (!categoryCode.isEmpty()) {
            if (!categoryCode.matches("\\d+")) {
                throw new G2bBusinessException("코드는 숫자만 입력 가능합니다.");
            }
            if (categoryCode.length() < 2) {
                throw new G2bBusinessException("코드는 최소 2자 이상 입력해 주세요.");
            }
        }

        // 분류명 검증: 입력했다면 최소 2자 이상
        if (!categoryName.isEmpty() && categoryName.length() < 2) {
            throw new G2bBusinessException("최소 2자 이상 입력해 주세요.");
        }

        return categoryRepository.findByFilters(categoryCode, categoryName, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<G2bItemJpaEntity> findItemList(String mCd, String dCd, String dNm, Pageable pageable) {
        // null 체크 및 공백 제거 처리
        String categoryCode = (mCd == null) ? "" : mCd.trim();
        String itemCode = (dCd == null) ? "" : dCd.trim();
        String itemName = (dNm == null) ? "" : dNm.trim();

        // 코드(분류/식별)가 둘 다 비어있다면, 명칭이 있더라도 검색 거부
        if (categoryCode.isEmpty() && itemCode.isEmpty()) {
            throw new G2bBusinessException("물품분류코드 또는 식별코드를 입력해 주세요.");
        }

        // 물품분류코드: 입력했다면 숫자 8자리여야 함
        if (!categoryCode.isEmpty() && !categoryCode.matches("\\d{8}")) {
            throw new G2bBusinessException("코드는 8자리 숫자만 입력 가능합니다.");
        }

        // 물품식별코드: 입력했다면 숫자 8자리여야 함
        if (!itemCode.isEmpty() && !itemCode.matches("\\d{8}")) {
            throw new G2bBusinessException("코드는 8자리 숫자만 입력 가능합니다.");
        }

        // 품목명 길이 검증: 입력했다면 최소 2자 이상
        if (!itemName.isEmpty() && itemName.length() < 2) {
            throw new G2bBusinessException("최소 2자 이상 입력해 주세요.");
        }

        // DB는 인덱스를 타고 명칭 필터를 추가로 수행함
        return itemRepository.findByFilters(categoryCode, itemCode, itemName, pageable);
    }
}