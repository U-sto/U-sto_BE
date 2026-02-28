package com.usto.api.organization.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.organization.application.DepartmentApplication;
import com.usto.api.organization.application.OrganizationApplication;
import com.usto.api.organization.presentation.dto.response.DepartmentResponse;
import com.usto.api.organization.presentation.dto.response.OrganizationResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "organization-controller", description = "조직 및 부서 관리 API")
@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationApplication organizationApplication;
    private final DepartmentApplication departmentApplication;

    /**
     * 조직 목록 조회 (회원가입 시 소속 선택용)
     */
    @Operation(
            summary = "조직 목록 조회",
            description = "시스템에 등록된 모든 조직 목록을 조회합니다. 회원가입 시 소속 선택에 사용됩니다."
    )
    @GetMapping("/organizations")
    public ApiResponse<List<OrganizationResponse>> getOrganizations() {
        return ApiResponse.ok("조직 목록 조회 성공",
                organizationApplication.getAllOrganizations());
    }

    /**
     * 부서 목록 조회 (본인 조직의 부서만)
     */
    @Operation(
            summary = "운용부서 목록 조회",
            description = "현재 로그인한 사용자가 속한 조직의 부서 목록을 조회합니다. (드롭박스에 사용)"
    )
    @GetMapping("/departments")
    public ApiResponse<List<DepartmentResponse>> getDepartments(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("부서 목록 조회 성공",
                departmentApplication.getDepartmentList(principal.getOrgCd()));
    }
}