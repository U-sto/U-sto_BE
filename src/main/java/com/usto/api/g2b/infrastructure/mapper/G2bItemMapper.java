package com.usto.api.g2b.infrastructure.mapper;

import com.usto.api.g2b.domain.model.G2bItem;
import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;

public class G2bItemMapper {

    public static G2bItem toDomain(G2bItemJpaEntity entity) {
        if (entity == null) return null;
        return G2bItem.builder()
                .g2bDCd(entity.getG2bDCd())
                .g2bMCd(entity.getG2bMCd())
                .g2bDNm(entity.getG2bDNm())
                .g2bUpr(entity.getG2bUpr())
                .build();

    }

    public static G2bItemJpaEntity toEntity(G2bItem domain) {
        if (domain == null) return null;
        return G2bItemJpaEntity.builder()
                .g2bDCd(domain.getG2bDCd())
                .g2bMCd(domain.getG2bMCd())
                .g2bDNm(domain.getG2bDNm())
                .g2bUpr(domain.getG2bUpr())
                .build();
    }

}