package com.usto.api.g2b.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class G2bItemCategoryJpaEntity
 * @desc 조달청 물품 분류 마스터 엔티티 (TB_G2B001M)
 */
@Entity
@Table(name = "TB_G2B001M")
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class G2bItemCategoryJpaEntity extends BaseTimeEntity {
    @Id
    // char(8)을 명시
    @Column(name = "G2B_M_CD", length = 8, columnDefinition = "char(8)")
    private String g2bMCd;

    @Column(name = "G2B_M_NM", length = 300, nullable = false)
    private String g2bMNm;
}