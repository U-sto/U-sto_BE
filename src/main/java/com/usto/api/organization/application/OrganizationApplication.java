package com.usto.api.organization.application;

import com.usto.api.organization.domain.repository.OrganizationRepository;
import com.usto.api.organization.presentation.dto.response.OrganizationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationApplication {

    private final OrganizationRepository organizationRepository;

    /**
     * 모든 조직 목록 조회 (회원가입 시 선택용)
     */
    @Transactional(readOnly = true)
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationRepository.findAll();
    }
}