package com.usto.api.g2b.domain.service;

import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;

import java.util.List;

public interface G2bItemService {

    int updateChangedPrices();

    List<G2bItemJpaEntity> findByFilters(
            String mCd,
            String dCd,
            String dNm
    );

}
