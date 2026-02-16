package com.usto.api.item.returning.infrastructure.adapter;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.returning.domain.model.ReturningDetail;
import com.usto.api.item.returning.domain.model.ReturningMaster;
import com.usto.api.item.returning.domain.repository.ReturningRepository;
import com.usto.api.item.returning.infrastructure.entity.ItemReturningDetailEntity;
import com.usto.api.item.returning.infrastructure.entity.QItemReturningDetailEntity;
import com.usto.api.item.returning.infrastructure.mapper.ReturningMapper;
import com.usto.api.item.returning.infrastructure.repository.ReturningDetailJpaRepository;
import com.usto.api.item.returning.infrastructure.repository.ReturningMasterJpaRepository;
import com.usto.api.item.returning.presentation.dto.request.ReturningSearchRequest;
import com.usto.api.item.returning.presentation.dto.response.ReturningItemListResponse;
import com.usto.api.item.returning.presentation.dto.response.ReturningListResponse;
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

import static com.usto.api.item.returning.infrastructure.entity.QItemReturningMasterEntity.itemReturningMasterEntity;
import static com.usto.api.item.returning.infrastructure.entity.QItemReturningDetailEntity.itemReturningDetailEntity;
import static com.usto.api.item.acquisition.infrastructure.entity.QItemAcquisitionEntity.itemAcquisitionEntity;
import static com.usto.api.item.asset.infrastructure.entity.QItemAssetEntity.itemAssetEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemJpaEntity.g2bItemJpaEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemCategoryJpaEntity.g2bItemCategoryJpaEntity;
import static com.usto.api.organization.infrastructure.entity.QDepartmentJpaEntity.departmentJpaEntity;

@Component
@RequiredArgsConstructor
public class ReturningRepositoryAdapter implements ReturningRepository {

    private final ReturningMasterJpaRepository masterJpaRepository;
    private final ReturningDetailJpaRepository detailJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public ReturningMaster saveMaster(ReturningMaster master) {
        var entity = ReturningMapper.toMasterEntity(master);
        var saved = masterJpaRepository.save(entity);
        return ReturningMapper.toMasterDomain(saved);
    }

    @Override
    public void saveDetail(ReturningDetail detail) {
        var entity = ReturningMapper.toDetailEntity(detail);
        detailJpaRepository.save(entity);
    }

    @Override
    public Optional<ReturningMaster> findMasterById(UUID rtrnMId, String orgCd) {
        return masterJpaRepository.findByRtrnMIdAndOrgCd(rtrnMId, orgCd)
                .map(ReturningMapper::toMasterDomain);
    }

    /**
     * 반납등록목록 조회 (마스터 + 등록자명 + 물품개수)
     */
    @Override
    public Page<ReturningListResponse> findAllByFilter(ReturningSearchRequest cond, String orgCd, Pageable pageable) {
        QUserJpaEntity user = QUserJpaEntity.userJpaEntity;

        // 1. Count 쿼리 (groupBy 없이)
        Long total = queryFactory
                .select(itemReturningMasterEntity.rtrnMId.countDistinct())
                .from(itemReturningMasterEntity)
                .leftJoin(itemReturningDetailEntity)
                .on(itemReturningMasterEntity.rtrnMId.eq(itemReturningDetailEntity.rtrnMId))
                .leftJoin(user)
                .on(itemReturningMasterEntity.aplyUsrId.eq(user.usrId))
                .where(
                        itemReturningMasterEntity.orgCd.eq(orgCd),
                        aplyAtBetween(cond.getStartAplyAt(), cond.getEndAplyAt()),
                        apprStsEq(cond.getApprSts())
                )
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        // 2. Data 쿼리 (groupBy 포함)
        List<ReturningListResponse> content = queryFactory
                .select(Projections.fields(ReturningListResponse.class,
                        itemReturningMasterEntity.rtrnMId.as("rtrnMId"),
                        itemReturningMasterEntity.aplyAt,
                        itemReturningMasterEntity.rtrnApprAt,
                        itemReturningMasterEntity.aplyUsrId,
                        user.usrNm.as("aplyUsrNm"),
                        itemReturningMasterEntity.apprSts.stringValue().as("apprSts"),
                        itemReturningDetailEntity.count().intValue().as("itemCount")
                ))
                .from(itemReturningMasterEntity)
                .leftJoin(itemReturningDetailEntity)
                .on(itemReturningMasterEntity.rtrnMId.eq(itemReturningDetailEntity.rtrnMId))
                .leftJoin(user)
                .on(itemReturningMasterEntity.aplyUsrId.eq(user.usrId))
                .where(
                        itemReturningMasterEntity.orgCd.eq(orgCd),
                        aplyAtBetween(cond.getStartAplyAt(), cond.getEndAplyAt()),
                        apprStsEq(cond.getApprSts())
                )
                .groupBy(itemReturningMasterEntity.rtrnMId)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemReturningMasterEntity.creAt.asc())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    /**
     * 반납물품목록 조회 (상세 + 대장기본/상세 + G2B + 부서 조인)
     */
    @Override
    public Page<ReturningItemListResponse> findItemsByMasterId(UUID rtrnMId, String orgCd, Pageable pageable) {
        // 1. 전체 개수 조회
        Long total = queryFactory
                .select(itemReturningDetailEntity.count())
                .from(itemReturningDetailEntity)
                .where(
                        itemReturningDetailEntity.rtrnMId.eq(rtrnMId),
                        itemReturningDetailEntity.orgCd.eq(orgCd)
                )
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        // 2. 데이터 조회 쿼리
        List<ReturningItemListResponse> content = queryFactory
                .select(Projections.fields(ReturningItemListResponse.class,
                        // G2B 목록번호 (분류코드-식별코드)
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemCategoryJpaEntity.g2bMCd,
                                itemAssetEntity.g2bDCd).as("g2bItemNo"),
                        // G2B 목록명
                        g2bItemJpaEntity.g2bDNm,
                        // 물품고유번호
                        itemReturningDetailEntity.itmNo,
                        // 취득일자 (취득기본)
                        itemAcquisitionEntity.acqAt,
                        // 취득금액 (대장상세)
                        itemAssetEntity.acqUpr,
                        // 운용부서명
                        departmentJpaEntity.deptNm.as("deptNm"),
                        // 물품상태 (마스터)
                        itemReturningMasterEntity.itemSts.stringValue().as("itemSts"),
                        // 반납사유 (마스터)
                        itemReturningMasterEntity.rtrnRsn.stringValue().as("rtrnRsn")
                ))
                .from(itemReturningDetailEntity)
                // 반납 마스터 조인 (물품상태, 사유)
                .join(itemReturningMasterEntity)
                .on(itemReturningDetailEntity.rtrnMId.eq(itemReturningMasterEntity.rtrnMId))
                // 대장상세 조인 (취득금액, G2B코드)
                .join(itemAssetEntity)
                .on(itemReturningDetailEntity.itmNo.eq(itemAssetEntity.itemId.itmNo),
                        itemReturningDetailEntity.orgCd.eq(itemAssetEntity.itemId.orgCd))
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
                .on(itemReturningDetailEntity.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemReturningDetailEntity.deptCd.eq(departmentJpaEntity.id.deptCd))
                .where(
                        itemReturningDetailEntity.rtrnMId.eq(rtrnMId),
                        itemReturningDetailEntity.orgCd.eq(orgCd)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemReturningDetailEntity.itmNo.asc())
                .fetch();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public void deleteMaster(UUID rtrnMId) {
        masterJpaRepository.deleteById(rtrnMId);
    }

    @Override
    public void deleteAllDetailsByMasterId(UUID rtrnMId) {
        detailJpaRepository.deleteAllByRtrnMId(rtrnMId);
    }

    /**
     * 중복 체크: 특정 물품이 다른 반납 신청서에 이미 등록되어 있는지 확인
     * @param itmNo 물품고유번호
     * @param excludeRtrnMId 제외할 반납ID (현재 수정 중인 신청서)
     * @param orgCd 조직코드
     * @return true = 다른 신청서에 이미 존재함 (중복)
     */
    @Override
    public boolean existsInOtherReturning(String itmNo, UUID excludeRtrnMId, String orgCd) {
        return queryFactory
                .selectOne()
                .from(itemReturningDetailEntity)
                .join(itemReturningMasterEntity)
                .on(itemReturningDetailEntity.rtrnMId.eq(itemReturningMasterEntity.rtrnMId))
                .where(
                        itemReturningDetailEntity.itmNo.eq(itmNo),
                        itemReturningDetailEntity.orgCd.eq(orgCd),
                        excludeRtrnMId != null ? itemReturningMasterEntity.rtrnMId.ne(excludeRtrnMId) : null,  // 현재 신청서는 제외
                        // 작성중 또는 요청중인 건만 중복 체크
                        itemReturningMasterEntity.apprSts.in(ApprStatus.WAIT, ApprStatus.REQUEST)
                )
                .fetchFirst() != null;
    }

    @Override
    public List<ReturningDetail> findDetailsByMasterId(UUID rtrnMId, String orgCd) {
        QItemReturningDetailEntity d = QItemReturningDetailEntity.itemReturningDetailEntity;

        List<ItemReturningDetailEntity> entities = queryFactory
                .selectFrom(d)
                .where(
                        d.rtrnMId.eq(rtrnMId),
                        d.orgCd.eq(orgCd)
                )
                .fetch();

        return entities.stream()
                .map(ReturningMapper::toDetailDomain)
                .toList();
    }

    /**
     * 특정 반납 신청서(Master)에 들어있는 물품 번호(itmNo)들을 찾는 메서드
     */
    @Override
    public List<String> findItemNosByMasterId(UUID rtrnMId, String orgCd) {
        return detailJpaRepository.findItemNosByRtrnMIdAndOrgCd(rtrnMId, orgCd);
    }

    // 동적 쿼리 헬퍼 메서드
    private BooleanExpression aplyAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemReturningMasterEntity.aplyAt.goe(s);
        if (s == null) return itemReturningMasterEntity.aplyAt.loe(e);
        return itemReturningMasterEntity.aplyAt.between(s, e);
    }

    private BooleanExpression apprStsEq(ApprStatus sts) {
        return sts != null ? itemReturningMasterEntity.apprSts.eq(sts) : null;
    }
}