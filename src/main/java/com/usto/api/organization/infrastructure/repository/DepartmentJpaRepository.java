package com.usto.api.organization.infrastructure.repository;

import com.usto.api.organization.infrastructure.entity.DepartmentJpaEntity;
import com.usto.api.organization.infrastructure.entity.DeptId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentJpaRepository extends JpaRepository<DepartmentJpaEntity, DeptId> {
    // 특정 조직코드(orgCd)에 해당하는 부서만 가나다순으로 조회
    List<DepartmentJpaEntity> findAllById_OrgCdOrderByDeptNmAsc(String orgCd);
}
