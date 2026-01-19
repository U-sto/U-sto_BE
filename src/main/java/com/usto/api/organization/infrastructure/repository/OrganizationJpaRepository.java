package com.usto.api.organization.infrastructure.repository;

import com.usto.api.organization.infrastructure.entity.OrganizationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrganizationJpaRepository extends JpaRepository<OrganizationJpaEntity, String> {
    Optional<OrganizationJpaEntity> findByOrgCd(String orgCd);

    @Query("SELECT o.orgNm FROM OrganizationJpaEntity o WHERE o.orgCd = :orgCd")
    Optional<String> findOrgNmByOrgCd(@Param("orgCd") String orgCd);}
