package com.usto.api.item.disuse.infrastructure.adapter;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.disuse.domain.model.DisuseDetail;
import com.usto.api.item.disuse.domain.model.DisuseMaster;
import com.usto.api.item.disuse.domain.repository.DisuseRepository;
import com.usto.api.item.disuse.infrastructure.entity.ItemDisuseDetailEntity;
import com.usto.api.item.disuse.infrastructure.entity.QItemDisuseDetailEntity;
import com.usto.api.item.disuse.infrastructure.mapper.DisuseMapper;
import com.usto.api.item.disuse.infrastructure.repository.DisuseDetailJpaRepository;
import com.usto.api.item.disuse.infrastructure.repository.DisuseMasterJpaRepository;
import com.usto.api.item.disuse.presentation.dto.request.DisuseSearchRequest;
import com.usto.api.item.disuse.presentation.dto.response.DisuseItemListResponse;
import com.usto.api.item.disuse.presentation.dto.response.DisuseListResponse;
import com.usto.api.item.returning.domain.model.ReturningDetail;
import com.usto.api.item.returning.infrastructure.entity.ItemReturningDetailEntity;
import com.usto.api.item.returning.infrastructure.entity.QItemReturningDetailEntity;
import com.usto.api.item.returning.infrastructure.mapper.ReturningMapper;
import com.usto.api.user.infrastructure.entity.QUserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.usto.api.item.disuse.infrastructure.entity.QItemDisuseMasterEntity.itemDisuseMasterEntity;
import static com.usto.api.item.disuse.infrastructure.entity.QItemDisuseDetailEntity.itemDisuseDetailEntity;
import static com.usto.api.item.asset.infrastructure.entity.QItemAssetDetailEntity.itemAssetDetailEntity;
import static com.usto.api.item.asset.infrastructure.entity.QItemAssetMasterEntity.itemAssetMasterEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemJpaEntity.g2bItemJpaEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemCategoryJpaEntity.g2bItemCategoryJpaEntity;
import static com.usto.api.organization.infrastructure.entity.QDepartmentJpaEntity.departmentJpaEntity;

@Component
@RequiredArgsConstructor
public class DisuseRepositoryAdapter implements DisuseRepository {
    private final DisuseMasterJpaRepository masterJpaRepository;
    private final DisuseDetailJpaRepository detailJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public DisuseMaster saveMaster(DisuseMaster master) {
        var entity = DisuseMapper.toMasterEntity(master);
        var saved = masterJpaRepository.save(entity);
        return DisuseMapper.toMasterDomain(saved);
    }

    @Override
    public void saveDetail(DisuseDetail detail) {
        var entity = DisuseMapper.toDetailEntity(detail);
        detailJpaRepository.save(entity);
    }

    @Override
    public Optional<DisuseMaster> findMasterById(UUID dsuMId, String orgCd) {
        return masterJpaRepository.findByDsuMIdAndOrgCd(dsuMId, orgCd)
                .map(DisuseMapper::toMasterDomain);
    }

    /**
     * 불용등록목록 조회 (페이징)
     */
    @Override
    public Page<DisuseListResponse> findAllByFilter(
            DisuseSearchRequest cond, String orgCd, Pageable pageable) {

        QUserJpaEntity user = QUserJpaEntity.userJpaEntity;

        // 1. Count 쿼리 (groupBy 없이)
        Long total = queryFactory
                .select(itemDisuseMasterEntity.dsuMId.countDistinct())
                .from(itemDisuseMasterEntity)
                .leftJoin(itemDisuseDetailEntity)
                .on(itemDisuseMasterEntity.dsuMId.eq(itemDisuseDetailEntity.dsuMId))
                .leftJoin(user)
                .on(itemDisuseMasterEntity.aplyUsrId.eq(user.usrId))
                .where(
                        itemDisuseMasterEntity.orgCd.eq(orgCd),
                        aplyAtBetween(cond.getStartAplyAt(), cond.getEndAplyAt()),
                        apprStsEq(cond.getApprSts())
                )
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        // 2. Data 쿼리 (groupBy 포함)
        List<DisuseListResponse> content = queryFactory
                .select(Projections.fields(DisuseListResponse.class,
                        itemDisuseMasterEntity.dsuMId.as("dsuMId"),
                        itemDisuseMasterEntity.aplyAt,
                        itemDisuseMasterEntity.dsuApprAt,
                        itemDisuseMasterEntity.aplyUsrId,
                        user.usrNm.as("aplyUsrNm"),  // 등록자명
                        itemDisuseMasterEntity.apprSts.stringValue().as("apprSts"),
                        itemDisuseDetailEntity.count().intValue().as("itemCount")
                ))
                .from(itemDisuseMasterEntity)
                .leftJoin(itemDisuseDetailEntity)
                .on(itemDisuseMasterEntity.dsuMId.eq(itemDisuseDetailEntity.dsuMId))
                .leftJoin(user)
                .on(itemDisuseMasterEntity.aplyUsrId.eq(user.usrId))
                .where(
                        itemDisuseMasterEntity.orgCd.eq(orgCd),
                        aplyAtBetween(cond.getStartAplyAt(), cond.getEndAplyAt()),
                        apprStsEq(cond.getApprSts())
                )
                .groupBy(itemDisuseMasterEntity.dsuMId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemDisuseMasterEntity.creAt.asc())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    /**
     * 불용물품목록 조회 (페이징)
     */
    @Override
    public Page<DisuseItemListResponse> findItemsByMasterId(
            UUID dsuMId, String orgCd, Pageable pageable) {
        // 1. 전체 개수 조회 (성능을 위해 조인을 최소화한 별도 쿼리)
        Long total = queryFactory
                .select(itemDisuseDetailEntity.count())
                .from(itemDisuseDetailEntity)
                .where(
                        itemDisuseDetailEntity.dsuMId.eq(dsuMId),
                        itemDisuseDetailEntity.orgCd.eq(orgCd)
                )
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        // 2. 데이터 조회 쿼리
        List<DisuseItemListResponse> content = queryFactory
                .select(Projections.fields(DisuseItemListResponse.class,
                        // G2B 목록번호 (분류코드-식별코드)
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemCategoryJpaEntity.g2bMCd,
                                itemAssetDetailEntity.g2bDCd).as("g2bItemNo"),
                        // G2B 목록명
                        g2bItemJpaEntity.g2bDNm,
                        // 물품고유번호
                        itemDisuseDetailEntity.itmNo,
                        // 취득일자 (대장기본)
                        itemAssetMasterEntity.acqAt,
                        // 취득금액 (대장상세)
                        itemAssetDetailEntity.acqUpr,
                        // 운용부서명
                        departmentJpaEntity.deptNm.as("deptNm"),
                        // 물품상태 (마스터)
                        itemDisuseMasterEntity.itemSts.stringValue().as("itemSts"),
                        // 사유 (마스터)
                        itemDisuseMasterEntity.dsuRsn.stringValue().as("dsuRsn")
                ))
                .from(itemDisuseDetailEntity)
                // 불용 마스터 조인
                .join(itemDisuseMasterEntity)
                .on(itemDisuseDetailEntity.dsuMId.eq(itemDisuseMasterEntity.dsuMId))
                // 대장상세 조인 (취득금액, G2B코드)
                .join(itemAssetDetailEntity)
                .on(itemDisuseDetailEntity.itmNo.eq(itemAssetDetailEntity.itemId.itmNo),
                        itemDisuseDetailEntity.orgCd.eq(itemAssetDetailEntity.itemId.orgCd))
                // 대장기본 조인 (취득일자)
                .join(itemAssetMasterEntity)
                .on(itemAssetDetailEntity.acqId.eq(itemAssetMasterEntity.acqId))
                // G2B 품목 조인 (품목명)
                .leftJoin(g2bItemJpaEntity)
                .on(itemAssetDetailEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd))
                // G2B 분류 조인 (분류코드)
                .leftJoin(g2bItemCategoryJpaEntity)
                .on(g2bItemJpaEntity.g2bMCd.eq(g2bItemCategoryJpaEntity.g2bMCd))
                // 부서 조인 (부서명)
                .leftJoin(departmentJpaEntity)
                .on(itemDisuseDetailEntity.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemDisuseDetailEntity.deptCd.eq(departmentJpaEntity.id.deptCd))
                .where(
                        itemDisuseDetailEntity.dsuMId.eq(dsuMId),
                        itemDisuseDetailEntity.orgCd.eq(orgCd)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemDisuseDetailEntity.itmNo.asc())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public void deleteMaster(UUID dsuMId) {
        masterJpaRepository.deleteById(dsuMId);
    }

    @Override
    public void deleteAllDetailsByMasterId(UUID dsuMId) {
        detailJpaRepository.deleteAllByDsuMId(dsuMId);
    }

    @Override
    public List<String> findItemNosByMasterId(UUID dsuMId, String orgCd) {
        return detailJpaRepository.findItemNosByDsuMIdAndOrgCd(dsuMId, orgCd);
    }

    @Override
    public List<String> findDuplicatedItems(
            List<String> itmNos, UUID excludeDsuMId, String orgCd
    ) {
        return queryFactory
                .select(itemDisuseDetailEntity.itmNo)
                .from(itemDisuseDetailEntity)
                .join(itemDisuseMasterEntity)
                .on(itemDisuseDetailEntity.dsuMId.eq(itemDisuseMasterEntity.dsuMId))
                .where(
                        itemDisuseDetailEntity.itmNo.in(itmNos),
                        itemDisuseDetailEntity.orgCd.eq(orgCd),
                        excludeDsuMId != null ?
                                itemDisuseMasterEntity.dsuMId.ne(excludeDsuMId) : null,
                        itemDisuseMasterEntity.apprSts.in(ApprStatus.REQUEST, ApprStatus.APPROVED)
                )
                .fetch();
    }

    @Override
    public void saveAllDetails(List<DisuseDetail> details) {
        List<ItemDisuseDetailEntity> entities = details.stream()
                .map(DisuseMapper::toDetailEntity)
                .toList();
        detailJpaRepository.saveAll(entities);  // Batch INSERT
    }


    @Override
    public List<DisuseDetail> findDetailsByMasterId(UUID dsuMId, String orgCd) {
        QItemDisuseDetailEntity d = QItemDisuseDetailEntity.itemDisuseDetailEntity;

        List<ItemDisuseDetailEntity> entities = queryFactory
                .selectFrom(d)
                .where(
                        d.dsuMId.eq(dsuMId),
                        d.orgCd.eq(orgCd)
                )
                .fetch();

        return entities.stream()
                .map(DisuseMapper::toDetailDomain)
                .toList();
    }


    // 동적 쿼리 헬퍼 메서드
    private BooleanExpression aplyAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemDisuseMasterEntity.aplyAt.goe(s);
        if (s == null) return itemDisuseMasterEntity.aplyAt.loe(e);
        return itemDisuseMasterEntity.aplyAt.between(s, e);
    }

    private BooleanExpression apprStsEq(ApprStatus sts) {
        return sts != null ? itemDisuseMasterEntity.apprSts.eq(sts) : null;
    }
}