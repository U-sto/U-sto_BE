package com.usto.api.item.asset.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemAssetId implements Serializable {
    @Column(name = "ITM_NO", length = 10, columnDefinition = "char(10)")
    private String itmNo;  // 물품고유번호 (M + 연도4자리 + 순번5자리)

    @Column(name = "ORG_CD", nullable = false, length = 7, columnDefinition = "char(7)")
    private String orgCd;
}