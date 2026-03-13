package com.usto.api.common.code.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "공통코드 그룹 응답")
public class CodeGroupResponse {

    @Schema(description = "그룹명", example = "승인상태")
    private String groupName;

    @Schema(description = "코드 목록")
    private List<CodeResponse> codes;
}