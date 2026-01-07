package com.usto.api.g2b.presentation.dto;

/**
 * @class G2bCategoryResponseDto
 * @desc 물품 분류 검색 결과 반환 DTO
 * @param mCd 물품분류코드
 * @param mNm 물품분류명
 */
public record G2bCategoryResponseDto(String mCd, String mNm) {}