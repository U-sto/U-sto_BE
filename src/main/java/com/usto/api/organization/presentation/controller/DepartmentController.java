package com.usto.api.organization.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.organization.application.DepartmentApplication;
import com.usto.api.organization.presentation.dto.response.DepartmentResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Organization", description = "조직 및 운용부서 API")
@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentApplication departmentApplication;

    @Operation(summary = "운용부서 목록 조회", description = "현재 로그인한 사용자가 속한 조직의 부서 목록을 조회합니다.")
    @GetMapping("/departments")
    public ApiResponse<List<DepartmentResponse>> getDepartments(@AuthenticationPrincipal UserPrincipal principal) {

        // 로그인한 유저의 조직코드 가져오기
        String myOrgCd = principal.getOrgCd();
        List<DepartmentResponse> list = departmentApplication.getDepartmentList(myOrgCd);

        return ApiResponse.ok("부서 목록 조회 성공", list);
    }
}