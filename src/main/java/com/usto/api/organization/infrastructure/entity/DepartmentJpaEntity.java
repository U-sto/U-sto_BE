package com.usto.api.organization.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "TB_ORG002M")
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepartmentJpaEntity extends BaseTimeEntity {

    @EmbeddedId
    private DeptId id; //ORG_CD+DEPT_CD

    @MapsId("orgCd")
    @ManyToOne(fetch = FetchType.LAZY) //운용부서 N : 조직 1
    @JoinColumn(name = "ORG_CD", nullable = false)
    private OrganizationJpaEntity organization;

    @Column(name = "DEPT_NM", length = 100, nullable = false)
    private String deptNm;

    @Column(name = "DEPT_TY", length = 20, nullable = false)
    private String deptTy;

    // 필요 시 생성 규칙 메서드로만 생성
    public static DepartmentJpaEntity of(
            OrganizationJpaEntity org,
            String deptCd,
            String deptNm,
            String deptTy
    ) {
        DepartmentJpaEntity e = new DepartmentJpaEntity();
        e.id = new DeptId(org.getOrgCd(), deptCd); //복합PK 완성
        e.organization = org;
        e.deptNm = deptNm;
        e.deptTy = deptTy;
        return e;
    }
}
