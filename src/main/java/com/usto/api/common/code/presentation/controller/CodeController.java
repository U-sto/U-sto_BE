package com.usto.api.common.code.presentation.controller;

import com.usto.api.common.code.application.CodeApplication;
import com.usto.api.common.code.presentation.dto.CodeGroupResponse;
import com.usto.api.common.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "code-controller", description = "공통코드 조회 API")
@RestController
@RequestMapping("/api/codes")
@RequiredArgsConstructor
public class CodeController {

    private final CodeApplication codeApplication;

    @Operation(
            summary = "공통코드 전체 조회",
            description = "시스템에서 사용하는 공통코드 그룹을 전부 조회합니다."
    )
    @GetMapping
    public ApiResponse<List<CodeGroupResponse>> getAllCodes() {
        return ApiResponse.ok("조회 성공", codeApplication.getAllCodeGroups());
    }

    @Operation(
            summary = "특정 공통코드 그룹 조회",
            description = """
            특정 공통코드 그룹을 조회합니다.
            
            사용 가능한 그룹명:
            - APPR_STATUS: 승인상태
            - OPER_STATUS: 운용상태
            - ITEM_STATUS: 물품상태
            - ACQ_ARRANGEMENT_TYPE: 취득정리구분
            - RETURNING_REASON: 반납사유
            - DISUSE_REASON: 불용사유
            - DISPOSAL_ARRANGEMENT_TYPE: 처분정리구분
            """
    )
    @GetMapping("/{groupName}")
    public ApiResponse<CodeGroupResponse> getCodeGroup(@PathVariable String groupName) {
        return ApiResponse.ok("조회 성공", codeApplication.getCodeGroup(groupName));
    }
}