package com.usto.api.g2b.infrastructure.entity;

import com.usto.api.g2b.domain.model.G2bItemCategory;

public class G2bItemCategoryMapper {

    public static G2bItemCategory toDomain(G2bItemCategoryJpaEntity entity) {
        if (entity == null) return null;
        return G2bItemCategory.builder()
                .g2bMCd(entity.getG2bMCd())
                .g2bMNm(entity.getG2bMNm())
                .build();
    }

    public static G2bItemCategoryJpaEntity toEntity(G2bItemCategory domain) {
        if (domain == null) return null;
        return G2bItemCategoryJpaEntity.builder()
                .g2bMCd(domain.getG2bMCd())
                .g2bMNm(domain.getG2bMNm())
                .build();
    }
}