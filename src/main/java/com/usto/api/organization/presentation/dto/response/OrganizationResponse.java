package com.usto.api.organization.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "조직 응답")
public class OrganizationResponse {

    @Schema(description = "조직코드", example = "7008277")
    private String orgCd;

    @Schema(description = "조직명", example = "한양대학교 ERICA 캠퍼스")
    private String orgNm;
}