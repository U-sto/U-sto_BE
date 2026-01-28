package com.usto.api.item.returning.infrastructure.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usto.api.item.common.model.ApprStatus;
import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.returning.domain.model.ReturningDetail;
import com.usto.api.item.returning.domain.model.ReturningMaster;
import com.usto.api.item.returning.domain.repository.ReturningRepository;
import com.usto.api.item.returning.infrastructure.mapper.ReturningMapper;
import com.usto.api.item.returning.presentation.dto.request.ReturningSearchRequest;
import com.usto.api.item.returning.presentation.dto.response.ReturningItemListResponse;
import com.usto.api.item.returning.presentation.dto.response.ReturningListResponse;
import com.usto.api.user.infrastructure.entity.QUserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.usto.api.item.returning.infrastructure.entity.QItemReturningMasterEntity.itemReturningMasterEntity;
import static com.usto.api.item.returning.infrastructure.entity.QItemReturningDetailEntity.itemReturningDetailEntity;
import static com.usto.api.item.asset.infrastructure.entity.QItemAssetDetailEntity.itemAssetDetailEntity;
import static com.usto.api.item.asset.infrastructure.entity.QItemAssetMasterEntity.itemAssetMasterEntity;
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
    public List<ReturningListResponse> findAllByFilter(ReturningSearchRequest cond, String orgCd) {
        QUserJpaEntity user = QUserJpaEntity.userJpaEntity;

        return queryFactory
                .select(Projections.fields(ReturningListResponse.class,
                        itemReturningMasterEntity.rtrnMId.as("rtrnMId"),
                        itemReturningMasterEntity.aplyAt,
                        itemReturningMasterEntity.rtrnApprAt,
                        itemReturningMasterEntity.aplyUsrId,
                        user.usrNm.as("aplyUsrNm"),  // 등록자명
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
                .orderBy(itemReturningMasterEntity.creAt.asc())
                .fetch();
    }

    /**
     * 반납물품목록 조회 (상세 + 대장기본/상세 + G2B + 부서 조인)
     */
    @Override
    public List<ReturningItemListResponse> findItemsByMasterId(UUID rtrnMId, String orgCd) {
        return queryFactory
                .select(Projections.fields(ReturningItemListResponse.class,
                        // G2B 목록번호 (분류코드-식별코드)
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemCategoryJpaEntity.g2bMCd,
                                itemAssetDetailEntity.g2bDCd).as("g2bItemNo"),
                        // G2B 목록명
                        g2bItemJpaEntity.g2bDNm,
                        // 물품고유번호
                        itemReturningDetailEntity.itmNo,
                        // 취득일자 (대장기본)
                        itemAssetMasterEntity.acqAt,
                        // 취득금액 (대장상세)
                        itemAssetDetailEntity.acqUpr,
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
                .join(itemAssetDetailEntity)
                .on(itemReturningDetailEntity.itmNo.eq(itemAssetDetailEntity.itemId.itmNo),
                        itemReturningDetailEntity.orgCd.eq(itemAssetDetailEntity.itemId.orgCd))
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
                .on(itemReturningDetailEntity.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemReturningDetailEntity.deptCd.eq(departmentJpaEntity.id.deptCd))
                .where(
                        itemReturningDetailEntity.rtrnMId.eq(rtrnMId),
                        itemReturningDetailEntity.orgCd.eq(orgCd)
                )
                .orderBy(itemReturningDetailEntity.itmNo.asc())
                .fetch();
    }

    /**
     * 이미 존재하는 반납 신청서에 포함되어 승인 대기 중인 물품인지 확인하는 메서드
     */
    @Override
    public boolean existsPendingReturnDetail(String itmNo, String orgCd) {
        return queryFactory
                .selectOne()
                .from(itemReturningDetailEntity)
                .join(itemReturningMasterEntity)
                .on(itemReturningDetailEntity.rtrnMId.eq(itemReturningMasterEntity.rtrnMId))
                .where(
                        itemReturningDetailEntity.itmNo.eq(itmNo),
                        itemReturningDetailEntity.orgCd.eq(orgCd),
                        itemReturningMasterEntity.apprSts.in(ApprStatus.WAIT, ApprStatus.REQUEST),
                        itemReturningMasterEntity.delYn.eq("N")
                )
                .fetchFirst() != null;
    }

    @Override
    public void deleteMaster(UUID rtrnMId) {
        masterJpaRepository.deleteById(rtrnMId);
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

    private BooleanExpression apprAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemReturningMasterEntity.rtrnApprAt.goe(s);
        if (s == null) return itemReturningMasterEntity.rtrnApprAt.loe(e);
        return itemReturningMasterEntity.rtrnApprAt.between(s, e);
    }

    private BooleanExpression itemStsEq(ItemStatus sts) {
        return sts != null ? itemReturningMasterEntity.itemSts.eq(sts) : null;
    }

    private BooleanExpression apprStsEq(ApprStatus sts) {
        return sts != null ? itemReturningMasterEntity.apprSts.eq(sts) : null;
    }
}