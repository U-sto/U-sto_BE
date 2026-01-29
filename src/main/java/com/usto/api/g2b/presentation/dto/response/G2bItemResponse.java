package com.usto.api.g2b.presentation.dto.response;

import java.math.BigDecimal;
/**
 * @class G2bItemResponseDto
 * @desc 세부 품목 검색 결과 반환 DTO
 * @param dCd 물품식별코드
 * @param dNm 물품품목명
 * @param upr 단가
 */
public record G2bItemResponse(String dCd, String dNm, BigDecimal upr) {}