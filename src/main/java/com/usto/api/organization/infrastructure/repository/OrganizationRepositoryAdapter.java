package com.usto.api.organization.infrastructure.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usto.api.organization.domain.repository.OrganizationRepository;
import com.usto.api.organization.presentation.dto.response.OrganizationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.usto.api.organization.infrastructure.entity.QOrganizationJpaEntity.organizationJpaEntity;

@Component
@RequiredArgsConstructor
public class OrganizationRepositoryAdapter implements OrganizationRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OrganizationResponse> findAll() {
        return queryFactory
                .select(Projections.fields(OrganizationResponse.class,
                        organizationJpaEntity.orgCd,
                        organizationJpaEntity.orgNm
                ))
                .from(organizationJpaEntity)
                // .where(organizationJpaEntity.delYn.eq("N"))
                .orderBy(organizationJpaEntity.orgNm.asc())  // 조직명 오름차순
                .fetch();
    }
}