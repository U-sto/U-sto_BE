package com.usto.api.organization.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable //복합키 (ORG_CD+DEPT_CD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeptId implements Serializable {

    @Column(name = "ORG_CD", length = 50, nullable = false)
    private String orgCd;

    @Column(name = "DEPT_CD", length = 50, nullable = false)
    private String deptCd;

    public DeptId(String orgCd, String deptCd) {
        this.orgCd = orgCd;
        this.deptCd = deptCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof DeptId)){
            return false;
        }

        DeptId that = (DeptId) o;
        return Objects.equals(orgCd, that.orgCd) && Objects.equals(deptCd, that.deptCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgCd, deptCd);
    }
}
