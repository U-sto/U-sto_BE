package com.usto.api.g2b.domain.repository;

import com.usto.api.g2b.infrastructure.entity.G2bItemCategoryJpaEntity;
import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;
import org.springframework.data.domain.Page;

/**
 * @class G2bSearchService
 * @desc G2B 데이터 검색 도메인 서비스 인터페이스
 */
public interface G2bSearchRepository {
    Page<G2bItemCategoryJpaEntity> findCategoryList(String code, String name, int page, int size);
    Page<G2bItemJpaEntity> findItemList(String mCd, String dCd, String dNm, int page, int size);
}