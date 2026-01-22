package com.usto.api.g2b.domain.service;

import com.usto.api.g2b.infrastructure.entity.G2bItemCategoryJpaEntity;

import java.util.List;

public interface G2bItemCategoryService {

    int updateMaster();

    List<G2bItemCategoryJpaEntity> findByFilters(
            String code,
            String name
    );
}
