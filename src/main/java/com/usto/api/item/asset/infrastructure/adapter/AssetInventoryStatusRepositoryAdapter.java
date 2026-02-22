package com.usto.api.item.asset.infrastructure.adapter;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usto.api.item.asset.domain.repository.AssetInventoryStatusRepository;
import com.usto.api.item.asset.presentation.dto.request.AssetInventoryStatusSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetInventoryStatusDetailResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetInventoryStatusListResponse;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.OperStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.usto.api.item.acquisition.infrastructure.entity.QItemAcquisitionEntity.itemAcquisitionEntity;
import static com.usto.api.item.asset.infrastructure.entity.QItemAssetEntity.itemAssetEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemJpaEntity.g2bItemJpaEntity;
import static com.usto.api.organization.infrastructure.entity.QDepartmentJpaEntity.departmentJpaEntity;

@Repository
@RequiredArgsConstructor
public class AssetInventoryStatusRepositoryAdapter implements AssetInventoryStatusRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AssetInventoryStatusListResponse> findAllByFilter(
            AssetInventoryStatusSearchRequest cond, String orgCd, Pageable pageable) {

        // 1. 데이터 조회 쿼리
        List<AssetInventoryStatusListResponse> content = queryFactory
                .select(Projections.fields(
                        AssetInventoryStatusListResponse.class,
                        itemAcquisitionEntity.acqId,
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemJpaEntity.g2bMCd,
                                itemAcquisitionEntity.g2bDCd).as("g2bItemNo"),
                        g2bItemJpaEntity.g2bDNm.as("g2bItemNm"),
                        itemAcquisitionEntity.acqAt,
                        itemAssetEntity.acqUpr,  // 개별 물품의 취득단가 (수정 가능한 값)
                        itemAcquisitionEntity.apprAt.as("arrgAt"),
                        departmentJpaEntity.deptNm.as("deptNm"),
                        itemAssetEntity.operSts.as("operSts"),
                        itemAssetEntity.drbYr,
                        itemAssetEntity.rmk,
                        itemAssetEntity.itemId.count().intValue().as("qty")
                ))
                .from(itemAcquisitionEntity)
                .innerJoin(itemAssetEntity).on(itemAssetEntity.acqId.eq(itemAcquisitionEntity.acqId))
                .leftJoin(g2bItemJpaEntity).on(
                        itemAcquisitionEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd)
                )
                .leftJoin(departmentJpaEntity).on(
                        itemAssetEntity.deptCd.eq(departmentJpaEntity.id.deptCd),
                        itemAssetEntity.itemId.orgCd.eq(departmentJpaEntity.id.orgCd)
                )
                .where(
                        itemAcquisitionEntity.orgCd.eq(orgCd),
                        itemAcquisitionEntity.apprSts.eq(ApprStatus.APPROVED),  // 승인된 건만
                        itemAssetEntity.delYn.eq("N"),         // 삭제되지 않은 물품만 (처분 제외)
                        g2bDCdEq(cond.getG2bDCd()),
                        deptCdEq(cond.getDeptCd()),
                        acqAtBetween(cond.getStartAcqAt(), cond.getEndAcqAt()),
                        apprAtBetween(cond.getStartApprAt(), cond.getEndApprAt())
                )
                .groupBy(
                        itemAcquisitionEntity.acqId,
                        itemAcquisitionEntity.g2bDCd,
                        g2bItemJpaEntity.g2bMCd,
                        g2bItemJpaEntity.g2bDNm,
                        itemAcquisitionEntity.acqAt,
                        itemAcquisitionEntity.apprAt,
                        departmentJpaEntity.deptNm,
                        itemAssetEntity.deptCd,    // ① 운용부서
                        itemAssetEntity.operSts,   // ② 운용상태
                        itemAssetEntity.acqUpr,    // ③ 취득금액 (개별 물품 단위)
                        itemAssetEntity.drbYr,     // ④ 내용연수
                        itemAssetEntity.rmk        // ⑤ 비고
                )
                .orderBy(
                        itemAcquisitionEntity.acqAt.desc(),
                        itemAcquisitionEntity.acqId.asc(),
                        departmentJpaEntity.deptNm.asc(),
                        itemAssetEntity.operSts.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. 전체 개수 조회 쿼리 - groupBy 결과의 크기를 카운트
        Long total = (long) queryFactory
                .select(itemAcquisitionEntity.acqId)
                .from(itemAcquisitionEntity)
                .innerJoin(itemAssetEntity).on(itemAssetEntity.acqId.eq(itemAcquisitionEntity.acqId))
                .leftJoin(g2bItemJpaEntity).on(
                        itemAcquisitionEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd)
                )
                .leftJoin(departmentJpaEntity).on(
                        itemAssetEntity.deptCd.eq(departmentJpaEntity.id.deptCd),
                        itemAssetEntity.itemId.orgCd.eq(departmentJpaEntity.id.orgCd)
                )
                .where(
                        itemAcquisitionEntity.orgCd.eq(orgCd),
                        itemAcquisitionEntity.apprSts.eq(ApprStatus.APPROVED),
                        itemAssetEntity.delYn.eq("N"),
                        g2bDCdEq(cond.getG2bDCd()),
                        deptCdEq(cond.getDeptCd()),
                        acqAtBetween(cond.getStartAcqAt(), cond.getEndAcqAt()),
                        apprAtBetween(cond.getStartApprAt(), cond.getEndApprAt())
                )
                .groupBy(
                        itemAcquisitionEntity.acqId,
                        itemAcquisitionEntity.g2bDCd,
                        g2bItemJpaEntity.g2bMCd,
                        g2bItemJpaEntity.g2bDNm,
                        itemAcquisitionEntity.acqAt,
                        itemAcquisitionEntity.apprAt,
                        departmentJpaEntity.deptNm,
                        itemAssetEntity.deptCd,
                        itemAssetEntity.operSts,
                        itemAssetEntity.acqUpr,
                        itemAssetEntity.drbYr,
                        itemAssetEntity.rmk
                )
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public AssetInventoryStatusDetailResponse findDetailByGroup(
            UUID acqId, String deptCd, OperStatus operSts,
            BigDecimal acqUpr, String drbYr, String rmk, String orgCd) {

        // 1. 물품고유번호 목록 조회
        List<String> itmNos = queryFactory
                .select(itemAssetEntity.itemId.itmNo)
                .from(itemAssetEntity)
                .where(
                        itemAssetEntity.acqId.eq(acqId),
                        itemAssetEntity.itemId.orgCd.eq(orgCd),
                        itemAssetEntity.deptCd.eq(deptCd),
                        itemAssetEntity.operSts.eq(operSts),
                        itemAssetEntity.acqUpr.eq(acqUpr),
                        itemAssetEntity.drbYr.eq(drbYr),
                        eqOrBothNull(itemAssetEntity.rmk, rmk),
                        itemAssetEntity.delYn.eq("N")
                )
                .fetch();

        // 2. 상세 정보 조회 (대표값 1건)
        AssetInventoryStatusDetailResponse detail = queryFactory
                .select(Projections.fields(
                        AssetInventoryStatusDetailResponse.class,
                        g2bItemJpaEntity.g2bDNm,
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemJpaEntity.g2bMCd,
                                itemAcquisitionEntity.g2bDCd).as("g2bItemNo"),
                        itemAcquisitionEntity.acqAt,
                        itemAcquisitionEntity.apprAt.as("arrgAt"),
                        itemAssetEntity.operSts,
                        itemAssetEntity.drbYr,
                        itemAssetEntity.acqUpr,
                        itemAcquisitionEntity.arrgTy.as("acqArrgTy"),
                        departmentJpaEntity.deptNm,
                        itemAssetEntity.deptCd,
                        itemAssetEntity.rmk
                ))
                .from(itemAcquisitionEntity)
                .innerJoin(itemAssetEntity).on(itemAssetEntity.acqId.eq(itemAcquisitionEntity.acqId))
                .leftJoin(g2bItemJpaEntity).on(
                        itemAcquisitionEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd)
                )
                .leftJoin(departmentJpaEntity).on(
                        itemAssetEntity.deptCd.eq(departmentJpaEntity.id.deptCd),
                        itemAssetEntity.itemId.orgCd.eq(departmentJpaEntity.id.orgCd)
                )
                .where(
                        itemAssetEntity.acqId.eq(acqId),
                        itemAssetEntity.itemId.orgCd.eq(orgCd),
                        itemAssetEntity.deptCd.eq(deptCd),
                        itemAssetEntity.operSts.eq(operSts),
                        itemAssetEntity.acqUpr.eq(acqUpr),
                        itemAssetEntity.drbYr.eq(drbYr),
                        eqOrBothNull(itemAssetEntity.rmk, rmk),
                        itemAssetEntity.delYn.eq("N")
                )
                .fetchFirst();

        // 3. 물품고유번호 목록 세팅
        if (detail != null) {
            return AssetInventoryStatusDetailResponse.builder()
                    .itmNos(itmNos)  // 리스트 추가
                    .g2bDNm(detail.getG2bDNm())
                    .g2bItemNo(detail.getG2bItemNo())
                    .acqAt(detail.getAcqAt())
                    .arrgAt(detail.getArrgAt())
                    .operSts(detail.getOperSts())
                    .drbYr(detail.getDrbYr())
                    .acqUpr(detail.getAcqUpr())
                    .qty(itmNos.size())
                    .acqArrgTy(detail.getAcqArrgTy())
                    .deptNm(detail.getDeptNm())
                    .deptCd(detail.getDeptCd())
                    .rmk(detail.getRmk())
                    .build();
        }

        return null;
    }

    // NULL 안전 비교 헬퍼 메서드
    private BooleanExpression eqOrBothNull(StringPath field, String value) {
        if (value == null) {
            // value가 null이면 → "field IS NULL" 조건 생성
            return field.isNull();
        }
        // value가 null이 아니면 → "field = value" 조건 생성
        return field.eq(value);
    }

    /**
     * 동적 쿼리를 위한 헬퍼 메서드들
     */
    private BooleanExpression g2bDCdEq(String cd) {
        return StringUtils.hasText(cd) ? itemAcquisitionEntity.g2bDCd.eq(cd) : null;
    }

    private BooleanExpression deptCdEq(String dept) {
        return StringUtils.hasText(dept) ? itemAssetEntity.deptCd.eq(dept) : null;
    }

    private BooleanExpression acqAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemAcquisitionEntity.acqAt.goe(s);
        if (s == null) return itemAcquisitionEntity.acqAt.loe(e);
        return itemAcquisitionEntity.acqAt.between(s, e);
    }

    private BooleanExpression apprAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemAcquisitionEntity.apprAt.goe(s);
        if (s == null) return itemAcquisitionEntity.apprAt.loe(e);
        return itemAcquisitionEntity.apprAt.between(s, e);
    }
}