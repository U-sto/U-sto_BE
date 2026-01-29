package com.usto.api.organization.application;

import com.usto.api.organization.infrastructure.repository.DepartmentJpaRepository;
import com.usto.api.organization.presentation.dto.response.DepartmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentApplication {
    private final DepartmentJpaRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getDepartmentList(String orgCd) {

        // 특정 조직의 부서만 조회하도록 필터링
        return departmentRepository.findAllById_OrgCdOrderByDeptNmAsc(orgCd).stream()
                .map(entity ->
                        DepartmentResponse.builder()
                        .orgCd(entity.getId().getOrgCd())
                        .deptCd(entity.getId().getDeptCd())
                        .deptNm(entity.getDeptNm())
                        .upDeptNm(entity.getUpDeptNm())
                        .build())
                .collect(Collectors.toList());
    }
}