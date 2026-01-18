package com.usto.api.item.acquisition.infrastructure.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.acquisition.domain.repository.AcquisitionRepository;
import com.usto.api.item.acquisition.infrastructure.entity.ItemAcquisitionEntity;
import com.usto.api.item.acquisition.presentation.dto.request.AcqSearchRequest;
import com.usto.api.item.acquisition.presentation.dto.response.AcqListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.usto.api.item.acquisition.infrastructure.entity.QItemAcquisitionEntity.itemAcquisitionEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemJpaEntity.g2bItemJpaEntity;
import static com.usto.api.organization.infrastructure.entity.QDepartmentJpaEntity.departmentJpaEntity;


@Component
@RequiredArgsConstructor
public class AcquisitionRepositoryAdapter implements AcquisitionRepository {

    private final AcquisitionJpaRepository jpaRepository;
    private final JPAQueryFactory queryFactory;

    /**
     * 물품 취득 정보 저장
     */
    @Override
    public ItemAcquisitionEntity save(ItemAcquisitionEntity entity) {
        return jpaRepository.save(entity);
    }

    /**
     * 취득ID로 단건 조회
     */
    @Override
    public Optional<ItemAcquisitionEntity> findById(Long id) {
        return jpaRepository.findById(id);
    }

    /**
     * 필터 조건에 따른 취득대장목록 검색
     * @param cond 검색 조건 (DTO)
     * @param orgCd 현재 로그인한 유저의 조직코드
     */
    @Override
    public List<AcqListResponse> findAllByFilter(AcqSearchRequest cond, String orgCd) {
        return queryFactory
                .select(Projections.fields(AcqListResponse.class,
                        itemAcquisitionEntity.acqId,
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemJpaEntity.g2bMCd,
                                itemAcquisitionEntity.g2bDCd).as("g2bItemNo"), // 목록번호 = [분류코드]-[식별코드] 포맷팅
                        g2bItemJpaEntity.g2bDNm.as("g2bItemNm"), // 조인한 G2B 테이블에서 품목명 가져옴
                        itemAcquisitionEntity.acqAt,
                        itemAcquisitionEntity.apprAt,
                        itemAcquisitionEntity.acqUpr,
                        departmentJpaEntity.deptNm.as("deptNm"), // 조인한 부서 테이블에서 코드 대신 '부서명'을 가져옴
                        itemAcquisitionEntity.drbYr,
                        itemAcquisitionEntity.acqQty,
                        itemAcquisitionEntity.operSts.stringValue().as("operSts"),
                        itemAcquisitionEntity.apprSts.stringValue().as("apprSts")
                ))
                .from(itemAcquisitionEntity)
                // G2B 품목 정보 조인 (품목명 등을 가져오기 위함)
                .leftJoin(g2bItemJpaEntity).on(
                        itemAcquisitionEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd)
                )
                // 부서 정보 조인 (조직코드 + 부서코드가 일치해야 함)
                .leftJoin(departmentJpaEntity).on(
                        itemAcquisitionEntity.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemAcquisitionEntity.deptCd.eq(departmentJpaEntity.id.deptCd)
                )
                .where(
                        itemAcquisitionEntity.orgCd.eq(orgCd), // 로그인한 사용자의 조직 데이터만 조회 (보안)
                        g2bDCdEq(cond.getG2bDCd()), // 식별코드 검색
                        deptCdEq(cond.getDeptCd()), // 부서 검색
                        acqAtBetween(cond.getStartAcqAt(), cond.getEndAcqAt()), // 취득일자 범위
                        apprAtBetween(cond.getStartApprAt(), cond.getEndApprAt()), // 정리일자 범위

                        // 비즈니스 로직: 정리일자 입력이 있으면 승인상태='확정'만 조회
                        approvedOnlyIfApprDate(cond.getStartApprAt(), cond.getEndApprAt()),
                        apprStsEq(cond.getApprSts())
                )
                .orderBy(itemAcquisitionEntity.acqId.desc())  // 최근 등록 순 정렬
                .fetch();
    }

    /**
     * 물품 취득 정보 삭제 (Soft Delete)
     * - 엔티티의 @SQLDelete 설정에 의해 내부적으로 UPDATE 쿼리가 실행됩니다.
     */
    @Override
    public void delete(ItemAcquisitionEntity entity) {
        jpaRepository.delete(entity);
    }

    /**
     * 동적 쿼리를 위한 헬퍼 메서드들 (값이 null이면 조건이 무시됨)
     */
    private BooleanExpression g2bDCdEq(String cd) {
        return StringUtils.hasText(cd) ? itemAcquisitionEntity.g2bDCd.eq(cd) : null;
    }
    private BooleanExpression deptCdEq(String dept) {
        return StringUtils.hasText(dept) ? itemAcquisitionEntity.deptCd.eq(dept) : null;
    }

    // 날짜 검색
    private BooleanExpression acqAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemAcquisitionEntity.acqAt.goe(s);
        if (s == null && e != null) return itemAcquisitionEntity.acqAt.loe(e);
        return itemAcquisitionEntity.acqAt.between(s, e);
    }
    private BooleanExpression apprAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemAcquisitionEntity.apprAt.goe(s);
        if (s == null && e != null) return itemAcquisitionEntity.apprAt.loe(e);
        return itemAcquisitionEntity.apprAt.between(s, e);
    }

    // 정리일자(정리시작일/종료일) 입력이 하나라도 있으면 승인상태는 무조건 'APPROVED'여야 함
    private BooleanExpression approvedOnlyIfApprDate(LocalDate s, LocalDate e) {
        return (s != null || e != null)
                ? itemAcquisitionEntity.apprSts.eq(ApprStatus.APPROVED)
                : null;
    }
    private BooleanExpression apprStsEq(ApprStatus s) {
        return s != null ? itemAcquisitionEntity.apprSts.eq(s) : null;
    }
}