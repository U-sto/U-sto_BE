package com.usto.api.g2b.domain.model;

import lombok.Builder;
import lombok.Getter;

//내부객체 - 변경을 위한 최소 정보
@Getter
@Builder
public class G2bStg {
    //private final long stgId; //순번
    private final String g2bMCd;   // 물품분류코드 -> G2B_M_CD
    private final String g2bMNm;   // 물품분류명   -> G2B_M_NM
    private final String g2bDCd;   // 물품식별코드 -> G2B_D_CD
    private final String g2bDNm;   // 물품품목명   -> G2B_D_NM
    private final long   g2bUpr;   // 단가
}
