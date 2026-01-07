package com.usto.api.organization.presentation.controller;

import com.usto.api.organization.application.DepartmentService;
import com.usto.api.organization.presentation.dto.response.DepartmentResponse;
import com.usto.api.user.domain.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Organization", description = "조직 및 운용부서 조회 API")
@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService deptService;

    @Operation(summary = "운용부서 목록 조회", description = "현재 로그인한 사용자가 속한 조직의 부서 목록을 조회합니다.")
    @GetMapping("/departments")
    public List<DepartmentResponse> getDepartments(@AuthenticationPrincipal UserPrincipal principal) {
        // String myOrgCd = principal.getOrgCd();

        // 개발환경에서 테스트 시 로그인 안 되어있다고 가정하고, 일단 ERICA 부서만 나오게 하드코딩
        String myOrgCd = "HANYANG_ERICA";

        return deptService.getDepartmentList(myOrgCd);
    }
}