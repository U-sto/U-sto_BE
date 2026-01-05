package com.usto.api.g2b.domain.service;

import com.usto.api.g2b.infrastructure.entity.G2BItemCategoryJpaEntity;
import com.usto.api.g2b.infrastructure.entity.G2BItemJpaEntity;
import java.util.List;

/**
 * @class G2BSearchService
 * @desc G2B 데이터 검색 도메인 서비스 인터페이스
 */
public interface G2BSearchService {
    List<G2BItemCategoryJpaEntity> findCategoryList(String code, String name);
    List<G2BItemJpaEntity> findItemList(String mCd, String dCd, String dNm);
}