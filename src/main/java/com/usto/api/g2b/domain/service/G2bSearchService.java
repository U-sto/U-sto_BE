package com.usto.api.g2b.domain.service;

import com.usto.api.g2b.infrastructure.entity.G2bItemCategoryJpaEntity;
import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;
import java.util.List;

/**
 * @class G2bSearchService
 * @desc G2B 데이터 검색 도메인 서비스 인터페이스
 */
public interface G2bSearchService {
    List<G2bItemCategoryJpaEntity> findCategoryList(String code, String name);
    List<G2bItemJpaEntity> findItemList(String mCd, String dCd, String dNm);
}