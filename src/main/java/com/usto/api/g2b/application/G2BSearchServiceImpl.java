package com.usto.api.g2b.application;

import com.usto.api.g2b.domain.service.G2BSearchService;
import com.usto.api.g2b.infrastructure.entity.G2BItemCategoryJpaEntity;
import com.usto.api.g2b.infrastructure.entity.G2BItemJpaEntity;
import com.usto.api.g2b.infrastructure.repository.G2BItemCategoryJpaRepository;
import com.usto.api.g2b.infrastructure.repository.G2BItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @class G2BSearchServiceImpl
 * @desc G2B 검색 비즈니스 로직 구현 클래스
 */
@Service
@RequiredArgsConstructor
public class G2BSearchServiceImpl implements G2BSearchService {
    private final G2BItemCategoryJpaRepository categoryRepository;
    private final G2BItemJpaRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<G2BItemCategoryJpaEntity> findCategoryList(String code, String name) {
        // null 체크 후 빈 문자열로 치환하여 LIKE '%%'가 모든 값을 포함하도록 유도
        String searchCode = (code == null) ? "" : code.trim();
        String searchName = (name == null) ? "" : name.trim();

        return categoryRepository.findByFilters(searchCode, searchName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<G2BItemJpaEntity> findItemList(String mCd, String dCd, String dNm) {
        if (mCd == null || mCd.isEmpty()) {
            throw new IllegalArgumentException("물품분류코드는 필수입니다.");
        }
        String itemCode = (dCd == null) ? "" : dCd.trim();
        String itemName = (dNm == null) ? "" : dNm.trim();
        return itemRepository.findByG2bMCdAndG2bDCdContainingAndG2bDNmContaining(
                mCd, itemCode, itemName);
    }
}