package com.usto.api.item.disposal.infrastructure.adapter;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.disposal.domain.model.DisposalDetail;
import com.usto.api.item.disposal.domain.model.DisposalMaster;
import com.usto.api.item.disposal.domain.model.DisposalArrangementType;
import com.usto.api.item.disposal.domain.repository.DisposalRepository;
import com.usto.api.item.disposal.infrastructure.entity.ItemDisposalDetailEntity;
import com.usto.api.item.disposal.infrastructure.entity.QItemDisposalDetailEntity;
import com.usto.api.item.disposal.infrastructure.mapper.DisposalMapper;
import com.usto.api.item.disposal.infrastructure.repository.DisposalDetailJpaRepository;
import com.usto.api.item.disposal.infrastructure.repository.DisposalMasterJpaRepository;
import com.usto.api.item.disposal.presentation.dto.request.DisposalSearchRequest;
import com.usto.api.item.disposal.presentation.dto.response.DisposalItemListResponse;
import com.usto.api.item.disposal.presentation.dto.response.DisposalListResponse;
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
import static com.usto.api.item.disposal.infrastructure.entity.QItemDisposalMasterEntity.itemDisposalMasterEntity;
import static com.usto.api.item.disposal.infrastructure.entity.QItemDisposalDetailEntity.itemDisposalDetailEntity;
import static com.usto.api.item.asset.infrastructure.entity.QItemAssetEntity.itemAssetEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemJpaEntity.g2bItemJpaEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemCategoryJpaEntity.g2bItemCategoryJpaEntity;

@Component
@RequiredArgsConstructor
public class DisposalRepositoryAdapter implements DisposalRepository {

    private final DisposalMasterJpaRepository masterJpaRepository;
    private final DisposalDetailJpaRepository detailJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public DisposalMaster saveMaster(DisposalMaster master) {
        var entity = DisposalMapper.toMasterEntity(master);
        var saved = masterJpaRepository.save(entity);
        return DisposalMapper.toMasterDomain(saved);
    }

    @Override
    public void saveDetail(DisposalDetail detail) {
        var entity = DisposalMapper.toDetailEntity(detail);
        detailJpaRepository.save(entity);
    }

    @Override
    public Optional<DisposalMaster> findMasterById(UUID dispMId, String orgCd) {
        return masterJpaRepository.findByDispMIdAndOrgCd(dispMId, orgCd)
                .map(DisposalMapper::toMasterDomain);
    }

    /**
     * 처분등록목록 조회 (페이징)
     */
    @Override
    public Page<DisposalListResponse> findAllByFilter(
            DisposalSearchRequest cond, String orgCd, Pageable pageable) {

        QUserJpaEntity user = QUserJpaEntity.userJpaEntity;

        // 1. Count 쿼리 최적화: 마스터 테이블만 사용하여 조인 제거
        Long total = queryFactory
                .select(itemDisposalMasterEntity.count())
                .from(itemDisposalMasterEntity)
                .where(
                        itemDisposalMasterEntity.orgCd.eq(orgCd),
                        AplyAtBetween(cond.getStartAplyAt(), cond.getEndAplyAt()),
                        dispTypeEq(cond.getDispType()),
                        apprStsEq(cond.getApprSts())
                )
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        // 2. Data 쿼리 (groupBy 포함)
        List<DisposalListResponse> content = queryFactory
                .select(Projections.fields(DisposalListResponse.class,
                        itemDisposalMasterEntity.dispMId.as("dispMId"),
                        itemDisposalMasterEntity.dispType.stringValue().as("dispType"),
                        itemDisposalMasterEntity.aplyAt,
                        itemDisposalMasterEntity.aplyUsrId,
                        user.usrNm.as("aplyUsrNm"),
                        itemDisposalMasterEntity.apprSts.stringValue().as("apprSts"),
                        itemDisposalDetailEntity.count().intValue().as("itemCount")
                ))
                .from(itemDisposalMasterEntity)
                .leftJoin(itemDisposalDetailEntity)
                .on(itemDisposalMasterEntity.dispMId.eq(itemDisposalDetailEntity.dispMId))
                .leftJoin(user)
                .on(itemDisposalMasterEntity.aplyUsrId.eq(user.usrId))
                .where(
                        itemDisposalMasterEntity.orgCd.eq(orgCd),
                        AplyAtBetween(cond.getStartAplyAt(), cond.getEndAplyAt()),
                        dispTypeEq(cond.getDispType()),
                        apprStsEq(cond.getApprSts())
                )
                .groupBy(itemDisposalMasterEntity.dispMId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemDisposalMasterEntity.creAt.asc())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    /**
     * 처분물품목록 조회 (페이징)
     */
    @Override
    public Page<DisposalItemListResponse> findItemsByMasterId(
            UUID dispMId, String orgCd, Pageable pageable) {

        // 1. 전체 개수 조회
        Long total = queryFactory
                .select(itemDisposalDetailEntity.count())
                .from(itemDisposalDetailEntity)
                .where(
                        itemDisposalDetailEntity.dispMId.eq(dispMId),
                        itemDisposalDetailEntity.orgCd.eq(orgCd)
                )
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        // 2. 데이터 조회 쿼리
        List<DisposalItemListResponse> content = queryFactory
                .select(Projections.fields(DisposalItemListResponse.class,
                        // G2B 목록번호
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemCategoryJpaEntity.g2bMCd,
                                itemAssetEntity.g2bDCd).as("g2bItemNo"),
                        // G2B 목록명
                        g2bItemJpaEntity.g2bDNm,
                        // 물품고유번호
                        itemDisposalDetailEntity.itmNo,
                        // 취득일자
                        itemAcquisitionEntity.acqAt,
                        // 취득금액
                        itemAssetEntity.acqUpr,
                        // 물품상태 (처분상세 테이블에서)
                        itemDisposalDetailEntity.itemSts.stringValue().as("itemSts"),
                        // 불용사유 (처분상세 테이블에서)
                        itemDisposalDetailEntity.chgRsn.stringValue().as("chgRsn"),
                        // 처분방식 (처분마스터에서)
                        itemDisposalMasterEntity.dispType.stringValue().as("dispType")
                ))
                .from(itemDisposalDetailEntity)
                // 처분 마스터 조인
                .join(itemDisposalMasterEntity)
                .on(itemDisposalDetailEntity.dispMId.eq(itemDisposalMasterEntity.dispMId))
                // 대장상세 조인
                .join(itemAssetEntity)
                .on(itemDisposalDetailEntity.itmNo.eq(itemAssetEntity.itemId.itmNo),
                        itemDisposalDetailEntity.orgCd.eq(itemAssetEntity.itemId.orgCd))
                // 취득기본 조인
                .join(itemAcquisitionEntity)
                .on(itemAssetEntity.acqId.eq(itemAcquisitionEntity.acqId))
                // G2B 품목 조인
                .leftJoin(g2bItemJpaEntity)
                .on(itemAssetEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd))
                // G2B 분류 조인
                .leftJoin(g2bItemCategoryJpaEntity)
                .on(g2bItemJpaEntity.g2bMCd.eq(g2bItemCategoryJpaEntity.g2bMCd))
                .where(
                        itemDisposalDetailEntity.dispMId.eq(dispMId),
                        itemDisposalDetailEntity.orgCd.eq(orgCd)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemDisposalDetailEntity.itmNo.asc())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public void deleteMaster(UUID dispMId) {
        masterJpaRepository.deleteById(dispMId);
    }

    @Override
    public void deleteAllDetailsByMasterId(UUID dispMId) {
        detailJpaRepository.deleteAllByDispMId(dispMId);
    }

    @Override
    public List<String> findItemNosByMasterId(UUID dispMId, String orgCd) {
        return detailJpaRepository.findItemNosByDispMIdAndOrgCd(dispMId, orgCd);
    }

    /**
     * 중복 체크: 특정 물품이 다른 처분 신청서에 이미 등록되어 있는지 확인
     */
    @Override
    public List<String> findDuplicatedItems(
            List<String> itmNos, UUID excludeDispMId, String orgCd
    ) {
        return queryFactory
                .select(itemDisposalDetailEntity.itmNo)
                .from(itemDisposalDetailEntity)
                .join(itemDisposalMasterEntity)
                .on(itemDisposalDetailEntity.dispMId.eq(itemDisposalMasterEntity.dispMId))
                .where(
                        itemDisposalDetailEntity.itmNo.in(itmNos),
                        itemDisposalDetailEntity.orgCd.eq(orgCd),
                        excludeDispMId != null ?
                                itemDisposalMasterEntity.dispMId.ne(excludeDispMId) : null,
                        itemDisposalMasterEntity.apprSts.in(ApprStatus.REQUEST, ApprStatus.APPROVED)
                )
                .fetch();
    }

    @Override
    public void saveAllDetails(List<DisposalDetail> details) {
        List<ItemDisposalDetailEntity> entities = details.stream()
                .map(DisposalMapper::toDetailEntity)
                .toList();
        detailJpaRepository.saveAll(entities);
    }

    @Override
    public List<DisposalDetail> findDetailsByMasterId(UUID dispMId, String orgCd) {
        QItemDisposalDetailEntity d = QItemDisposalDetailEntity.itemDisposalDetailEntity;

        List<ItemDisposalDetailEntity> entities = queryFactory
                .selectFrom(d)
                .where(
                        d.dispMId.eq(dispMId),
                        d.orgCd.eq(orgCd)
                )
                .fetch();

        return entities.stream()
                .map(DisposalMapper::toDetailDomain)
                .toList();
    }

    // 동적 쿼리 헬퍼 메서드
    private BooleanExpression AplyAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemDisposalMasterEntity.aplyAt.goe(s);
        if (s == null) return itemDisposalMasterEntity.aplyAt.loe(e);
        return itemDisposalMasterEntity.aplyAt.between(s, e);
    }

    private BooleanExpression dispTypeEq(DisposalArrangementType type) {
        return type != null ? itemDisposalMasterEntity.dispType.eq(type) : null;
    }

    private BooleanExpression apprStsEq(ApprStatus sts) {
        return sts != null ? itemDisposalMasterEntity.apprSts.eq(sts) : null;
    }
}