package com.usto.api.g2b.infrastructure.mapper;

import com.usto.api.common.utils.PrdctUsefulLifeEnvelope;
import com.usto.api.g2b.domain.model.G2bUsrfulList;
import com.usto.api.g2b.infrastructure.entity.G2bUsrfulListJpaEntity;

public class G2bUsrfulListMapper {


    //외부 data -> domain
    public static G2bUsrfulList toDomain(PrdctUsefulLifeEnvelope.Item item) {
        if (item == null) {
            return null;
        }
        return G2bUsrfulList.builder()
                .g2bMcd(item.prdctClsfcNo())
                .g2bMNm(item.prdctClsfcNoNm())
                .drbYr(item.uslfsvc())
                .build();
    }

    /**
     * Entity(DB) -> Domain(Business)
     * 데이터베이스에서 조회한 데이터를 도메인 객체로 변환
     */
    public static G2bUsrfulList toDomain(G2bUsrfulListJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return G2bUsrfulList.builder()
                .g2bMcd(entity.getG2bMCd())
                .g2bMNm(entity.getG2bMNm())
                .drbYr(entity.getDrbYr())
                .build();
    }

    /**
     * Domain(Business) -> Entity(DB)
     * 도메인 로직 처리 결과를 데이터베이스 저장을 위해 엔티티로 변환
     */
    public static G2bUsrfulListJpaEntity toEntity(G2bUsrfulList domain) {
        if (domain == null) {
            return null;
        }
        return G2bUsrfulListJpaEntity.builder()
                .g2bMCd(domain.getG2bMcd())
                .g2bMNm(domain.getG2bMNm())
                .drbYr(domain.getDrbYr())
                .build();
    }
}
