package com.usto.api.g2b.presentation.dto;

import java.math.BigDecimal;
/**
 * @class G2bItemResponseDto
 * @desc 세부 품목 검색 결과 반환 DTO
 */
public record G2bItemResponseDto(String dCd, String dNm, BigDecimal upr) {}
