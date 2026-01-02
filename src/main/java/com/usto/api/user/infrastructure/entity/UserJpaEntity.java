package com.usto.api.user.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import com.usto.api.organization.infrastructure.entity.OrganizationJpaEntity;
import com.usto.api.user.domain.model.ApprovalStatus;
import com.usto.api.user.domain.model.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_USER001M")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 사용할 기본 생성자 만들기
public class UserJpaEntity extends BaseTimeEntity {

    // 로그인 아이디
    @Id //PK
    @Column(name = "USR_ID", length = 50,nullable = false) //PK긴 하지만 가독성을 위해
    private String usrId;

    //유저이름
    @Column(name = "USR_NM", length = 50, nullable = false)
    private String usrNm;

    // 비밀번호
    // 암호화된 문자열이 저장
    @Column(name = "PW_HASH",length = 255, nullable = false)
    private String pwHash;

    // 이메일
    @Column(name = "EMAIL", length = 255 , unique = true, nullable = false)
    private String email;

    // 전화번호
    @Column(name = "SMS", length = 11, unique = true, nullable = false ,columnDefinition = "CHAR(11)")
    private String sms;

    //역할E
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE_ID", length = 20, nullable = false)
    private Role roleId = Role.GUEST; //기본 값은 GUEST(승인 전)

    //조직코드
    @Column(name = "ORG_CD" , length = 50 , nullable = false)
    private String orgCd;

    //승인여부
    @Enumerated(EnumType.STRING)
    @Column(name = "APPR_STS" , length = 20 , nullable = false)
    private ApprovalStatus apprSts = ApprovalStatus.WAIT; //기본 값은 WAIT(승인 전)

    //승인자ID - 셀프 조인을 할까? 했는데 단순 이력이라서 그냥 컬럼으로 사용
    @Column(name = "APPR_USR_ID", length = 50)
    private String apprUsrId;

    //승인일자
    @Column(name = "APPR_AT")
    private LocalDateTime apprAt;

    // 조직 엔티티 연관관계 - 조직명 등 필요할 때만 조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_CD", referencedColumnName = "ORG_CD",
            insertable = false, updatable = false)
    private OrganizationJpaEntity organization; // <-조회용 “읽기 전용 뷰
}
