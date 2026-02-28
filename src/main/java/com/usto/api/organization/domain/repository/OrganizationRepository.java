package com.usto.api.organization.domain.repository;

import com.usto.api.organization.presentation.dto.response.OrganizationResponse;

import java.util.List;

public interface OrganizationRepository {
    /**
     * 모든 조직 목록 조회
     */
    List<OrganizationResponse> findAll();
}