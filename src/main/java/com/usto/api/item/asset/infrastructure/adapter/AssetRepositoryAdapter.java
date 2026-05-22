package com.usto.api.item.asset.infrastructure.adapter;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usto.api.g2b.infrastructure.entity.QG2bItemCategoryJpaEntity;
import com.usto.api.g2b.infrastructure.entity.QG2bItemJpaEntity;
import com.usto.api.item.acquisition.infrastructure.entity.QItemAcquisitionEntity;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetStatusHistory;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.asset.infrastructure.entity.*;
import com.usto.api.item.asset.infrastructure.mapper.AssetMapper;
import com.usto.api.item.asset.infrastructure.repository.AssetJpaRepository;
import com.usto.api.item.asset.infrastructure.repository.AssetStatusHistoryJpaRepository;
import com.usto.api.item.asset.presentation.dto.request.AssetListForPrintRequest;
import com.usto.api.item.asset.presentation.dto.request.AssetSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.*;
import com.usto.api.item.common.model.OperStatus;
import com.usto.api.organization.infrastructure.entity.QDepartmentJpaEntity;
import com.usto.api.organization.infrastructure.entity.QOrganizationJpaEntity;
import com.usto.api.user.infrastructure.entity.QUserJpaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.usto.api.item.acquisition.infrastructure.entity.QItemAcquisitionEntity.itemAcquisitionEntity;
import static com.usto.api.item.asset.infrastructure.entity.QItemAssetEntity.itemAssetEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemJpaEntity.g2bItemJpaEntity;
import static com.usto.api.organization.infrastructure.entity.QDepartmentJpaEntity.departmentJpaEntity;

/**
 * AssetRepository 구현체
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AssetRepositoryAdapter implements AssetRepository {

    private final AssetJpaRepository jpaRepository;
    private final AssetStatusHistoryJpaRepository statusHistoryJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Asset save(Asset asset) {
        ItemAssetEntity entity = AssetMapper.toEntity(asset);
        ItemAssetEntity saved = jpaRepository.save(entity);
        return AssetMapper.toDomain(saved);
    }

    @Override
    public Optional<Asset> findById(String itmNo, String orgCd) {
        return jpaRepository.findById(new ItemAssetId(itmNo, orgCd))
                .map(AssetMapper::toDomain);
    }

    @Override
    public List<Asset> findAllById(List<String> itmNos, String orgCd) {
        if (itmNos == null || itmNos.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemAssetEntity> entities = queryFactory
                .selectFrom(itemAssetEntity)
                .where(
                        itemAssetEntity.itemId.itmNo.in(itmNos),
                        itemAssetEntity.itemId.orgCd.eq(orgCd)
                )
                .fetch();

        return entities.stream()
                .map(AssetMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(String itmNo, String orgCd) {
        jpaRepository.deleteById(new ItemAssetId(itmNo, orgCd));
    }


    /**
     * 필터 조건에 따른 물품대장 검색
     * @param cond  검색 조건 (DTO)
     * @param orgCd 현재 로그인한 유저의 조직코드
     */
    @Override
    public Page<AssetListResponse> findAllByFilter(AssetSearchRequest cond, String orgCd, Pageable pageable) {
        // 1. Count 쿼리
        Long total = queryFactory
                .select(itemAssetEntity.count())
                .from(itemAssetEntity)
                .leftJoin(itemAcquisitionEntity).on(
                        itemAssetEntity.acqId.eq(itemAcquisitionEntity.acqId)
                )
                .leftJoin(g2bItemJpaEntity).on(
                        itemAssetEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd)
                )
                .leftJoin(departmentJpaEntity).on(
                        itemAssetEntity.itemId.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemAssetEntity.deptCd.eq(departmentJpaEntity.id.deptCd)
                )
                .where(
                        itemAssetEntity.itemId.orgCd.eq(orgCd),
                        g2bDCdEq(cond.getG2bDCd()),
                        acqAtBetween(cond.getStartAcqAt(), cond.getEndAcqAt()),
                        arrgAtBetween(cond.getStartArrgAt(), cond.getEndArrgAt()),
                        deptCdEq(cond.getDeptCd()),
                        operStsEq(cond.getOperSts()),
                        itmNoEq(cond.getItmNo()),
                        itemAssetEntity.delYn.eq("N")
                )
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        // 2. Data 쿼리
        List<AssetListResponse> content = queryFactory
                .select(Projections.fields(AssetListResponse.class,
                        itemAssetEntity.itemId.itmNo,
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemJpaEntity.g2bMCd,
                                itemAssetEntity.g2bDCd).as("g2bItemNo"),
                        g2bItemJpaEntity.g2bDNm.as("g2bItemNm"),
                        itemAcquisitionEntity.acqAt,
                        itemAssetEntity.acqUpr,
                        itemAcquisitionEntity.apprAt.as("arrgAt"), // 정리일자
                        departmentJpaEntity.deptNm.as("deptNm"),
                        itemAssetEntity.operSts.as("operSts"),
                        itemAssetEntity.drbYr
                ))
                .from(itemAssetEntity)
                .leftJoin(itemAcquisitionEntity).on(
                        itemAssetEntity.acqId.eq(itemAcquisitionEntity.acqId)
                )
                .leftJoin(g2bItemJpaEntity).on(
                        itemAssetEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd)
                )
                .leftJoin(departmentJpaEntity).on(
                        itemAssetEntity.itemId.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemAssetEntity.deptCd.eq(departmentJpaEntity.id.deptCd)
                )
                .where(
                        itemAssetEntity.itemId.orgCd.eq(orgCd),
                        g2bDCdEq(cond.getG2bDCd()),
                        acqAtBetween(cond.getStartAcqAt(), cond.getEndAcqAt()),
                        arrgAtBetween(cond.getStartArrgAt(), cond.getEndArrgAt()),
                        deptCdEq(cond.getDeptCd()),
                        operStsEq(cond.getOperSts()),
                        itmNoEq(cond.getItmNo()),
                        itemAssetEntity.delYn.eq("N")
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemAssetEntity.itemId.itmNo.desc()) // 물품고유번호 정렬
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Optional<AssetDetailResponse> findDetailById(String itmNo, String orgCd) {
        AssetDetailResponse detail = queryFactory
                .select(Projections.fields(AssetDetailResponse.class,
                        itemAssetEntity.itemId.itmNo,
                        g2bItemJpaEntity.g2bDNm.as("g2bDNm"),
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemJpaEntity.g2bMCd,
                                itemAssetEntity.g2bDCd).as("g2bItemNo"),
                        itemAcquisitionEntity.acqAt,
                        itemAcquisitionEntity.apprAt, // 정리일자
                        itemAssetEntity.operSts.as("operSts"),
                        itemAssetEntity.drbYr,
                        itemAssetEntity.acqUpr,
                        itemAcquisitionEntity.arrgTy.as("acqArrgTy"),
                        departmentJpaEntity.deptNm.as("deptNm"),
                        itemAssetEntity.deptCd,
                        itemAssetEntity.rmk
                ))
                .from(itemAssetEntity)
                .leftJoin(itemAcquisitionEntity).on(
                        itemAssetEntity.acqId.eq(itemAcquisitionEntity.acqId)
                )
                .leftJoin(g2bItemJpaEntity).on(
                        itemAssetEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd)
                )
                .leftJoin(departmentJpaEntity).on(
                        itemAssetEntity.itemId.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemAssetEntity.deptCd.eq(departmentJpaEntity.id.deptCd)
                )
                .where(
                        itemAssetEntity.itemId.itmNo.eq(itmNo),
                        itemAssetEntity.itemId.orgCd.eq(orgCd),
                        itemAssetEntity.delYn.eq("N")
                )
                .fetchOne();

        return Optional.ofNullable(detail);
    }


    @Override
    public void saveStatusHistory(AssetStatusHistory history) {
        ItemAssetStatusHistoryEntity entity = AssetMapper.toStatusHistoryEntity(history);
        statusHistoryJpaRepository.save(entity);
    }

    @Override
    public List<AssetDetailResponse.StatusHistoryDto> findStatusHistoriesByItmNo(String itmNo, String orgCd) {
        QItemAssetStatusHistoryEntity history = QItemAssetStatusHistoryEntity.itemAssetStatusHistoryEntity;
        QUserJpaEntity reqUser = new QUserJpaEntity("reqUser");
        QUserJpaEntity apprUser = new QUserJpaEntity("apprUser");

        return queryFactory
                .select(Projections.fields(AssetDetailResponse.StatusHistoryDto.class,
                        history.itemHisId,
                        history.itmNo,
                        history.prevSts,
                        history.newSts,
                        history.chgRsn,
                        history.reqUsrId,
                        reqUser.usrNm.as("reqUsrNm"),
                        history.reqAt,
                        history.apprUsrId,
                        apprUser.usrNm.as("apprUsrNm"),
                        history.apprAt
                ))
                .from(history)
                .leftJoin(reqUser).on(history.reqUsrId.eq(reqUser.usrId))
                .leftJoin(apprUser).on(history.apprUsrId.eq(apprUser.usrId))
                .where(
                        history.itmNo.eq(itmNo),
                        history.orgCd.eq(orgCd),
                        history.delYn.eq("N")
                )
                .orderBy(
                        history.apprAt.asc(),
                        history.creAt.asc()
                )
                .fetch();
    }

    /**
     * 자산 중복 생성 방지용 검사 메서드
     */
    @Override
    public boolean existsAssetByAcqId(UUID acqId) {
        return queryFactory
                .selectOne()
                .from(itemAssetEntity)
                .where(itemAssetEntity.acqId.eq(acqId))
                .fetchFirst() != null;
    }

    /**
     * 신규 물품번호 생성을 위한 다음 순번 조회 TODO: DB 레벨에서 원자적으로 순번을 발급하도록 개선 필요
     */
    @Override
    public int getNextSequenceForYear(int year, String orgCd) {
        int maxSequence = jpaRepository.findMaxSequenceByYear(String.valueOf(year), orgCd);
        return maxSequence + 1;
    }

    @Override
    public List<AssetAiItemDetailResponse> findAllByG2bCode(String g2bMCd, String g2bDCd, String orgCd) {
        return jpaRepository.findAllByG2bCode(g2bMCd, g2bDCd, orgCd);
    }

    @Override
    public List<AssetAiItemDetailResponse> findAllByG2bName(String g2bDNm, String orgCd) {
        return jpaRepository.findAllByG2bName(g2bDNm, orgCd);
    }

    @Override
    public List<AssetAiItemDetailResponse> findAllByG2bDCd(String g2bDCd, String orgCd) {
        return jpaRepository.findAllByG2bDCd(g2bDCd, orgCd);
    }

    @Override
    public List<AssetAiItemDetailResponse> findAllByG2bMCd(String g2bMCd, String orgCd) {
        return jpaRepository.findAllByG2bMCd(g2bMCd, orgCd);
    }

    @Override
    public List<AssetAiItemDetailResponse> findOneByItmNo(String itmNo, String orgCd) {
        return jpaRepository.findOneByItmNo(itmNo, orgCd);
    }

    @Override
    public Asset findAssetById(String itmNo, String orgCd){
        QItemAssetEntity d = QItemAssetEntity.itemAssetEntity;

        ItemAssetEntity entity = queryFactory
                .selectFrom(d)
                .where(
                        d.itemId.itmNo.eq(itmNo),
                        d.itemId.orgCd.eq(orgCd),
                        d.delYn.eq("N")
                )
                .fetchOne();

        return entity != null ? AssetMapper.toDomain(entity) : null;
    }

    @Override
    public void saveAll(List<Asset> assetsToUpdate) {
        List<ItemAssetEntity> entities = assetsToUpdate
                .stream()
                .map(AssetMapper :: toEntity) // 매핑 메서드 필요
                .toList();
        jpaRepository.saveAll(entities);
    }

    @Override
    public AssetPublicDetailResponse  findPublicDetailByItmNoAndOrgCd(String itmNo, String orgCd) {
        QItemAcquisitionEntity a = QItemAcquisitionEntity.itemAcquisitionEntity;
        QItemAssetEntity d = QItemAssetEntity.itemAssetEntity;
        QG2bItemJpaEntity g = QG2bItemJpaEntity.g2bItemJpaEntity;
        QG2bItemCategoryJpaEntity c = QG2bItemCategoryJpaEntity.g2bItemCategoryJpaEntity;
        QDepartmentJpaEntity dept = QDepartmentJpaEntity.departmentJpaEntity;
        QOrganizationJpaEntity org = QOrganizationJpaEntity.organizationJpaEntity;

        AssetPublicDetailResponse result =
                (queryFactory
                .select(Projections.fields(
                        AssetPublicDetailResponse.class,
                        d.itemId.itmNo.as("itmNo"),
                        org.orgNm.as("orgNm"),
                        g.g2bDNm.as("g2bDNm"),
                        Expressions.stringTemplate(
                                "CONCAT({0}, '-', {1})",
                                c.g2bMCd, g.g2bDCd
                        ).as("g2bItemNo"),
                        d.acqUpr.as("acqUpr"),
                        a.acqAt.as("acqAt"),
                        a.apprAt.as("arrgAt"),
                        d.operSts.as("operSts"),
                        d.drbYr.as("drbYr"),
                        dept.deptNm.as("deptNm"),
                        ExpressionUtils.as(countQtySubQuery(), "qty"),
                        d.rmk.as("rmk")
                ))
                .from(d)
                .leftJoin(a).on(a.acqId.eq(d.acqId).and(a.delYn.eq("N")))
                .leftJoin(g).on(g.g2bDCd.eq(d.g2bDCd))
                .leftJoin(c).on(c.g2bMCd.eq(g.g2bMCd))
                .leftJoin(dept).on(dept.id.deptCd.eq(d.deptCd).and(dept.id.orgCd.eq(d.itemId.orgCd)))
                .leftJoin(org).on(org.orgCd.eq(d.itemId.orgCd))
                .where(
                        d.itemId.itmNo.eq(itmNo),
                        d.itemId.orgCd.eq(orgCd),
                        d.delYn.eq("N")
                )
                .fetchOne()
                );

        return result;
    }

    @Override
    public Page<AssetListForPrintResponse> findAllByFilterForPrint(AssetListForPrintRequest searchRequest, String orgCd, Pageable pageable) {
        // 1. Count 쿼리
        Long total = queryFactory
                .select(itemAssetEntity.count())
                .from(itemAssetEntity)
                .leftJoin(itemAcquisitionEntity).on(
                        itemAssetEntity.acqId.eq(itemAcquisitionEntity.acqId)
                )
                .leftJoin(g2bItemJpaEntity).on(
                        itemAssetEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd)
                )
                .leftJoin(departmentJpaEntity).on(
                        itemAssetEntity.itemId.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemAssetEntity.deptCd.eq(departmentJpaEntity.id.deptCd)
                )
                .where(
                        itemAssetEntity.itemId.orgCd.eq(orgCd),
                        g2bDCdEq(searchRequest.getG2bDCd()),
                        acqAtBetween(searchRequest.getStartAcqAt(), searchRequest.getEndAcqAt()),
                        arrgAtBetween(searchRequest.getStartArrgAt(), searchRequest.getEndArrgAt()),
                        deptCdEq(searchRequest.getDeptCd()),
                        operStsEq(searchRequest.getOperSts()),
                        itmNoEq(searchRequest.getItmNo()),
                        printYnEq(searchRequest.getPrintYn()),
                        itemAssetEntity.delYn.eq("N")
                )
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        // 2. Data 쿼리
        List<AssetListForPrintResponse> content = queryFactory
                .select(Projections.fields(AssetListForPrintResponse.class,
                        itemAssetEntity.itemId.itmNo,
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemJpaEntity.g2bMCd,
                                itemAssetEntity.g2bDCd).as("g2bItemNo"),
                        g2bItemJpaEntity.g2bDNm.as("g2bItemNm"),
                        itemAcquisitionEntity.acqAt,
                        itemAssetEntity.acqUpr,
                        itemAcquisitionEntity.apprAt.as("arrgAt"),
                        departmentJpaEntity.deptNm.as("deptNm"),
                        itemAssetEntity.operSts.as("operSts"),
                        itemAssetEntity.drbYr,
                        itemAssetEntity.printYn.as("printYn")
                ))
                .from(itemAssetEntity)
                .leftJoin(itemAcquisitionEntity).on(
                        itemAssetEntity.acqId.eq(itemAcquisitionEntity.acqId)
                )
                .leftJoin(g2bItemJpaEntity).on(
                        itemAssetEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd)
                )
                .leftJoin(departmentJpaEntity).on(
                        itemAssetEntity.itemId.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemAssetEntity.deptCd.eq(departmentJpaEntity.id.deptCd)
                )
                .where(
                        itemAssetEntity.itemId.orgCd.eq(orgCd),
                        g2bDCdEq(searchRequest.getG2bDCd()),
                        acqAtBetween(searchRequest.getStartAcqAt(), searchRequest.getEndAcqAt()),
                        arrgAtBetween(searchRequest.getStartArrgAt(), searchRequest.getEndArrgAt()),
                        deptCdEq(searchRequest.getDeptCd()),
                        operStsEq(searchRequest.getOperSts()),
                        itmNoEq(searchRequest.getItmNo()),
                        printYnEq(searchRequest.getPrintYn()),
                        itemAssetEntity.delYn.eq("N")
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemAssetEntity.itemId.itmNo.desc()) // 물품고유번호 정렬
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    @Transactional
    public void bulkDisposal(List<Asset> assets, String userId, String orgCd) {
        List<String> itemNos = assets.stream()
                .map(Asset::getItmNo)   // 실제 메서드명에 맞게
                .distinct()
                .toList();
        OperStatus newOperSts = OperStatus.DISP;
        jpaRepository.bulkDisposal(itemNos, userId, orgCd,newOperSts);
    }

    @Override
    @Transactional
    public void bulkDisuse(List<Asset> assets, String userId, String orgCd) {
        List<String> itemNos = assets.stream()
                .map(Asset::getItmNo)   // 실제 메서드명에 맞게
                .distinct()
                .toList();
        OperStatus newOperSts = OperStatus.DSU;
        jpaRepository.bulkDisuse(itemNos, userId, orgCd,newOperSts);
    }

    @Override
    @Transactional
    public void bulkReturning(List<Asset> assets, String userId, String orgCd) {
        List<String> itemNos = assets.stream()
                .map(Asset::getItmNo)   // 실제 메서드명에 맞게
                .distinct()
                .toList();
        OperStatus newOperSts = OperStatus.RTN;
        jpaRepository.bulkReturning(itemNos, userId, orgCd, newOperSts);
    }

    @Override
    @Transactional
    public void bulkUpdateForOperation(List<Asset> assets, String userId, String orgCd) {
        List<ItemAssetEntity> entities = assets.stream()
                .map(asset -> {
                    ItemAssetEntity entity = AssetMapper.toEntity(asset);
                    entity.setUpdBy(userId);
                    return entity;
                })
                .toList();

        jpaRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void bulkSoftDelete(List<Asset> assets, String userId, String orgCd) {
        List<String> itemNos = assets.stream()
                .map(Asset::getItmNo)   // 실제 메서드명에 맞게
                .distinct()
                .toList();
        jpaRepository.bulkSoftDelete(itemNos, userId, orgCd);
    }

    // ===== 동적 쿼리 헬퍼 =====

    /**
     * 특정 취득ID에 속한 '현재' 자산 수량을 카운트하는 서브쿼리 표현식
     */
    private JPQLQuery<Long> countQtySubQuery() {
        QItemAssetEntity subAsset = new QItemAssetEntity("subAsset");
        return JPAExpressions.select(subAsset.count())
                .from(subAsset)
                .where(subAsset.acqId.eq(itemAssetEntity.acqId) // 메인 쿼리의 acqId와 연결
                        .and(subAsset.delYn.eq("N")));
    }

    private BooleanExpression g2bDCdEq(String cd) {
        return StringUtils.hasText(cd) ? itemAssetEntity.g2bDCd.eq(cd) : null;
    }

    private BooleanExpression acqAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemAcquisitionEntity.acqAt.goe(s);
        if (s == null) return itemAcquisitionEntity.acqAt.loe(e);
        return itemAcquisitionEntity.acqAt.between(s, e);
    }

    private BooleanExpression arrgAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemAcquisitionEntity.apprAt.goe(s);
        if (s == null) return itemAcquisitionEntity.apprAt.loe(e);
        return itemAcquisitionEntity.apprAt.between(s, e);
    }

    private BooleanExpression deptCdEq(String dept) {
        return StringUtils.hasText(dept) ? itemAssetEntity.deptCd.eq(dept) : null;
    }

    private BooleanExpression operStsEq(OperStatus sts) {
        return sts != null ? itemAssetEntity.operSts.eq(sts) : null;
    }

    private BooleanExpression itmNoEq(String itmNo) {
        return StringUtils.hasText(itmNo) ? itemAssetEntity.itemId.itmNo.eq(itmNo) : null;
    }

    private BooleanExpression printYnEq(String printYn) {
        return (printYn == null || printYn.isBlank())
                ? null
                : itemAssetEntity.printYn.eq(printYn);
    }

}