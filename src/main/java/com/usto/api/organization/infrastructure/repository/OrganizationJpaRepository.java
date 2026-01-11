package com.usto.api.organization.infrastructure.repository;

import com.usto.api.organization.infrastructure.entity.OrganizationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationJpaRepository extends JpaRepository<OrganizationJpaEntity, String> {
    Optional<OrganizationJpaEntity> findByOrgCd(String orgCd);
}
