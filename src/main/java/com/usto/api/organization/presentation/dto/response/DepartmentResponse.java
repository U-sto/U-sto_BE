package com.usto.api.organization.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentResponse {
    private String orgCd;   // 복합키 내 조직코드
    private String deptCd;  // 복합키 내 부서코드
    private String deptNm;  // 부서명
    private String upDeptNm;  // 상위부서이름
}