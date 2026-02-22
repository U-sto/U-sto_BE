package com.usto.api.item.operation.infrastructure.adapter;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.operation.domain.model.OperationDetail;
import com.usto.api.item.operation.domain.model.OperationMaster;
import com.usto.api.item.operation.domain.repository.OperationRepository;
import com.usto.api.item.operation.infrastructure.entity.ItemOperationDetailEntity;
import com.usto.api.item.operation.infrastructure.entity.QItemOperationDetailEntity;
import com.usto.api.item.operation.infrastructure.mapper.OperationMapper;
import com.usto.api.item.operation.infrastructure.repository.OperationDetailJpaRepository;
import com.usto.api.item.operation.infrastructure.repository.OperationMasterJpaRepository;
import com.usto.api.item.operation.presentation.dto.request.OperationSearchRequest;
import com.usto.api.item.operation.presentation.dto.response.OperationItemListResponse;
import com.usto.api.item.operation.presentation.dto.response.OperationListResponse;
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

import static com.usto.api.item.acquisition.infrastructure.entity.QItemAcquisitionEntity.itemAcquisitionEntity;
import static com.usto.api.item.operation.infrastructure.entity.QItemOperationMasterEntity.itemOperationMasterEntity;
import static com.usto.api.item.operation.infrastructure.entity.QItemOperationDetailEntity.itemOperationDetailEntity;
import static com.usto.api.item.asset.infrastructure.entity.QItemAssetEntity.itemAssetEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemJpaEntity.g2bItemJpaEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemCategoryJpaEntity.g2bItemCategoryJpaEntity;
import static com.usto.api.organization.infrastructure.entity.QDepartmentJpaEntity.departmentJpaEntity;

@Component
@RequiredArgsConstructor
public class OperationRepositoryAdapter implements OperationRepository {
    private final OperationMasterJpaRepository masterJpaRepository;
    private final OperationDetailJpaRepository detailJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public OperationMaster saveMaster(OperationMaster master) {
        var entity = OperationMapper.toMasterEntity(master);
        var saved = masterJpaRepository.save(entity);
        return OperationMapper.toMasterDomain(saved);
    }

    @Override
    public void saveDetail(OperationDetail detail) {
        var entity = OperationMapper.toDetailEntity(detail);
        detailJpaRepository.save(entity);
    }

    @Override
    public Optional<OperationMaster> findMasterById(UUID operMId, String orgCd) {
        return masterJpaRepository.findByOperMIdAndOrgCd(operMId, orgCd)
                .map(OperationMapper::toMasterDomain);
    }

    /**
     * 운용등록목록 조회 (페이징)
     */
    @Override
    public Page<OperationListResponse> findAllByFilter(
            OperationSearchRequest cond, String orgCd, Pageable pageable) {

        QUserJpaEntity user = QUserJpaEntity.userJpaEntity;

        // 1. Count 쿼리 (groupBy 없이)
        Long total = queryFactory
                .select(itemOperationMasterEntity.operMId.countDistinct())
                .from(itemOperationMasterEntity)
                .leftJoin(itemOperationDetailEntity)
                .on(itemOperationMasterEntity.operMId.eq(itemOperationDetailEntity.operMId))
                .leftJoin(user)
                .on(itemOperationMasterEntity.aplyUsrId.eq(user.usrId))
                .where(
                        itemOperationMasterEntity.orgCd.eq(orgCd),
                        aplyAtBetween(cond.getStartAplyAt(), cond.getEndAplyAt()),
                        apprStsEq(cond.getApprSts())
                )
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        // 2. Data 쿼리 (groupBy 포함)
        List<OperationListResponse> content = queryFactory
                .select(Projections.fields(OperationListResponse.class,
                        itemOperationMasterEntity.operMId.as("operMId"),
                        itemOperationMasterEntity.aplyAt,
                        itemOperationMasterEntity.operApprAt,
                        itemOperationMasterEntity.aplyUsrId,
                        user.usrNm.as("aplyUsrNm"),  // 등록자명
                        itemOperationMasterEntity.apprSts.as("apprSts"),
                        itemOperationDetailEntity.count().intValue().as("itemCount")
                ))
                .from(itemOperationMasterEntity)
                .leftJoin(itemOperationDetailEntity)
                .on(itemOperationMasterEntity.operMId.eq(itemOperationDetailEntity.operMId))
                .leftJoin(user)
                .on(itemOperationMasterEntity.aplyUsrId.eq(user.usrId))
                .where(
                        itemOperationMasterEntity.orgCd.eq(orgCd),
                        aplyAtBetween(cond.getStartAplyAt(), cond.getEndAplyAt()),
                        apprStsEq(cond.getApprSts())
                )
                .groupBy(itemOperationMasterEntity.operMId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemOperationMasterEntity.creAt.asc())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    /**
     * 운용물품목록 조회 (페이징)
     */
    @Override
    public Page<OperationItemListResponse> findItemsByMasterId(
            UUID operMId, String orgCd, Pageable pageable) {
        // 1. 전체 개수 조회 (성능을 위해 조인을 최소화한 별도 쿼리)
        Long total = queryFactory
                .select(itemOperationDetailEntity.count())
                .from(itemOperationDetailEntity)
                .where(
                        itemOperationDetailEntity.operMId.eq(operMId),
                        itemOperationDetailEntity.orgCd.eq(orgCd)
                )
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        // 2. 데이터 조회 쿼리
        List<OperationItemListResponse> content = queryFactory
                .select(Projections.fields(OperationItemListResponse.class,
                        // G2B 목록번호 (분류코드-식별코드)
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemCategoryJpaEntity.g2bMCd,
                                itemAssetEntity.g2bDCd).as("g2bItemNo"),
                        // G2B 목록명
                        g2bItemJpaEntity.g2bDNm,
                        // 물품고유번호
                        itemOperationDetailEntity.itmNo,
                        // 취득일자 (취득기본)
                        itemAcquisitionEntity.acqAt,
                        // 취득금액 (대장상세)
                        itemAssetEntity.acqUpr,
                        // 운용부서명
                        departmentJpaEntity.deptNm.as("deptNm"),
                        // 물품상태 (마스터)
                        itemOperationMasterEntity.itemSts.as("itemSts")
                ))
                .from(itemOperationDetailEntity)
                // 운용 마스터 조인
                .join(itemOperationMasterEntity)
                .on(itemOperationDetailEntity.operMId.eq(itemOperationMasterEntity.operMId))
                // 대장상세 조인 (취득금액, G2B코드)
                .join(itemAssetEntity)
                .on(itemOperationDetailEntity.itmNo.eq(itemAssetEntity.itemId.itmNo),
                        itemOperationDetailEntity.orgCd.eq(itemAssetEntity.itemId.orgCd))
                // 취득기본 조인 (취득일자)
                .join(itemAcquisitionEntity)
                .on(itemAssetEntity.acqId.eq(itemAcquisitionEntity.acqId))
                // G2B 품목 조인 (품목명)
                .leftJoin(g2bItemJpaEntity)
                .on(itemAssetEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd))
                // G2B 분류 조인 (분류코드)
                .leftJoin(g2bItemCategoryJpaEntity)
                .on(g2bItemJpaEntity.g2bMCd.eq(g2bItemCategoryJpaEntity.g2bMCd))
                // 부서 조인 (부서명)
                .leftJoin(departmentJpaEntity)
                .on(itemOperationMasterEntity.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemOperationMasterEntity.deptCd.eq(departmentJpaEntity.id.deptCd))
                .where(
                        itemOperationDetailEntity.operMId.eq(operMId),
                        itemOperationDetailEntity.orgCd.eq(orgCd)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemOperationDetailEntity.itmNo.asc())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public void deleteMaster(UUID operMId) {
        masterJpaRepository.deleteById(operMId);
    }

    @Override
    public void deleteAllDetailsByMasterId(UUID operMId) {
        detailJpaRepository.deleteAllByOperMId(operMId);
    }

    @Override
    public List<String> findItemNosByMasterId(UUID operMId, String orgCd) {
        return detailJpaRepository.findItemNosByOperMIdAndOrgCd(operMId, orgCd);
    }

    @Override
    public List<String> findDuplicatedItems(
            List<String> itmNos, UUID excludeOperMId, String orgCd
    ) {
        return queryFactory
                .select(itemOperationDetailEntity.itmNo)
                .from(itemOperationDetailEntity)
                .join(itemOperationMasterEntity)
                .on(itemOperationDetailEntity.operMId.eq(itemOperationMasterEntity.operMId))
                .where(
                        itemOperationDetailEntity.itmNo.in(itmNos),
                        itemOperationDetailEntity.orgCd.eq(orgCd),
                        excludeOperMId != null ?
                                itemOperationMasterEntity.operMId.ne(excludeOperMId) : null,
                        itemOperationMasterEntity.apprSts.in(ApprStatus.WAIT, ApprStatus.REQUEST)
                )
                .fetch();
    }

    @Override
    public void saveAllDetails(List<OperationDetail> details) {
        List<ItemOperationDetailEntity> entities = details.stream()
                .map(OperationMapper::toDetailEntity)
                .toList();
        detailJpaRepository.saveAll(entities);  // Batch INSERT
    }

    @Override
    public List<OperationDetail> findDetailsByMasterId(UUID operMId, String orgCd) {
        QItemOperationDetailEntity d = QItemOperationDetailEntity.itemOperationDetailEntity;

        List<ItemOperationDetailEntity> entities = queryFactory
                .selectFrom(d)
                .where(
                        d.operMId.eq(operMId),
                        d.orgCd.eq(orgCd)
                )
                .fetch();

        return entities.stream()
                .map(OperationMapper::toDetailDomain)
                .toList();
    }

    // 동적 쿼리 헬퍼 메서드
    private BooleanExpression aplyAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemOperationMasterEntity.aplyAt.goe(s);
        if (s == null) return itemOperationMasterEntity.aplyAt.loe(e);
        return itemOperationMasterEntity.aplyAt.between(s, e);
    }

    private BooleanExpression apprStsEq(ApprStatus sts) {
        return sts != null ? itemOperationMasterEntity.apprSts.eq(sts) : null;
    }
}