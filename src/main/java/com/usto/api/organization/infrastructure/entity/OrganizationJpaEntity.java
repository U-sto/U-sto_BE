package com.usto.api.organization.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ORG001M")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //생성자 대체

public class OrganizationJpaEntity extends BaseTimeEntity{

    //조직코드
    @Id
    @Column(name = "ORG_CD", length = 50, nullable = false)
    private String orgCd;

    //조직명
    @Column(name = "ORG_NM", length = 100, nullable = false)
    private String orgNm;

}
