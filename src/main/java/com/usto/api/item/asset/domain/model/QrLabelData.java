package com.usto.api.item.asset.domain.model;

import lombok.Builder;
import lombok.Getter;

/**
 * QR 라벨 출력을 위한 데이터 모델
 * - 라벨에는 물품고유번호만 표시
 * - QR 코드는 상세 페이지 URL 포함
 */
@Getter
@Builder
public class QrLabelData {
    private String itmNo;           // 물품고유번호 (라벨에 표시)
    private String orgNm;           // 조직명 (추가)
    private String qrContent;       // QR 코드 내용 (URL)
}
