package com.usto.api.item.asset.infrastructure.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usto.api.common.exception.BusinessException;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetMaster;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetDetailEntity;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetMasterEntity;
import com.usto.api.item.asset.infrastructure.mapper.AssetMapper;
import com.usto.api.item.asset.presentation.dto.request.AssetSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetListResponse;
import com.usto.api.item.common.model.OperStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.usto.api.item.asset.infrastructure.entity.QItemAssetDetailEntity.itemAssetDetailEntity;
import static com.usto.api.item.asset.infrastructure.entity.QItemAssetMasterEntity.itemAssetMasterEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemJpaEntity.g2bItemJpaEntity;
import static com.usto.api.organization.infrastructure.entity.QDepartmentJpaEntity.departmentJpaEntity;

@Component
@RequiredArgsConstructor
public class AssetRepositoryAdapter implements AssetRepository {

    private final AssetJpaRepository jpaRepository;
    private final AssetMasterJpaRepository jpaMasterRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Asset save(Asset asset) {
        ItemAssetDetailEntity entity = AssetMapper.toEntity(asset);
        ItemAssetDetailEntity saved = jpaRepository.save(entity);
        return AssetMapper.toDomain(saved);
    }

    @Override
    public void saveMaster(AssetMaster master) {
        ItemAssetMasterEntity entity = AssetMapper.toMasterEntity(master);
        jpaMasterRepository.save(entity);
    }

    @Override
    public Optional<Asset> findById(String itmNo) {
        return jpaRepository.findById(itmNo).map(AssetMapper::toDomain);
    }

    /**
     * 필터 조건에 따른 운용대장목록 검색
     * @param cond 검색 조건 (DTO)
     * @param orgCd 현재 로그인한 유저의 조직코드
     */
    @Override
    public List<AssetListResponse> findAllByFilter(AssetSearchRequest cond, String orgCd) {
        return queryFactory
                .select(Projections.fields(AssetListResponse.class,
                        itemAssetDetailEntity.itmNo,
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemJpaEntity.g2bMCd,
                                itemAssetDetailEntity.g2bDCd).as("g2bItemNo"),
                        g2bItemJpaEntity.g2bDNm.as("g2bItemNm"),
                        itemAssetMasterEntity.acqAt,
                        itemAssetDetailEntity.acqUpr,
                        itemAssetMasterEntity.arrgAt,
                        departmentJpaEntity.deptNm.as("deptNm"),
                        itemAssetDetailEntity.operSts.stringValue().as("operSts"),
                        itemAssetDetailEntity.drbYr
                ))
                .from(itemAssetDetailEntity)
                // 대장기본 조인 (취득일자, 정리일자)
                .leftJoin(itemAssetMasterEntity).on(
                        itemAssetDetailEntity.acqId.eq(itemAssetMasterEntity.acqId)
                )
                // G2B 품목 조인
                .leftJoin(g2bItemJpaEntity).on(
                        itemAssetDetailEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd)
                )
                // 부서 조인
                .leftJoin(departmentJpaEntity).on(
                        itemAssetDetailEntity.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemAssetDetailEntity.deptCd.eq(departmentJpaEntity.id.deptCd)
                )
                .where(
                        itemAssetDetailEntity.orgCd.eq(orgCd),
                        g2bDCdEq(cond.getG2bDCd()),
                        acqAtBetween(cond.getStartAcqAt(), cond.getEndAcqAt()),
                        arrgAtBetween(cond.getStartArrgAt(), cond.getEndArrgAt()),
                        deptCdEq(cond.getDeptCd()),
                        operStsEq(cond.getOperSts()),
                        itmNoEq(cond.getItmNo())
                )
                .orderBy(itemAssetDetailEntity.itmNo.desc())
                .fetch();
    }

    /**
     * 신규 물품번호 생성을 위한 다음 순번 조회
     * - JpaRepository의 Native Query를 호출하여 DB의 MAX값 조회
     */
    @Override
    public int getNextSequenceForYear(int year, String orgCd) {
        int maxSequence = jpaRepository.findMaxSequenceByYear(year, orgCd);
        return maxSequence + 1;
    }

    @Override
    public void delete(String itmNo) {
        jpaRepository.deleteById(itmNo);
    }

    /**
     * 동적 쿼리를 위한 헬퍼 메서드들 (값이 null이면 조건이 무시됨)
     */
    private BooleanExpression g2bDCdEq(String cd) {
        return StringUtils.hasText(cd) ? itemAssetDetailEntity.g2bDCd.eq(cd) : null;
    }

    private BooleanExpression acqAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemAssetMasterEntity.acqAt.goe(s);
        if (s == null && e != null) return itemAssetMasterEntity.acqAt.loe(e);
        return itemAssetMasterEntity.acqAt.between(s, e);
    }

    private BooleanExpression arrgAtBetween(LocalDate s, LocalDate e) {
        if (s == null && e == null) return null;
        if (s != null && e == null) return itemAssetMasterEntity.arrgAt.goe(s);
        if (s == null && e != null) return itemAssetMasterEntity.arrgAt.loe(e);
        return itemAssetMasterEntity.arrgAt.between(s, e);
    }

    private BooleanExpression deptCdEq(String dept) {
        return StringUtils.hasText(dept) ? itemAssetDetailEntity.deptCd.eq(dept) : null;
    }

    private BooleanExpression operStsEq(OperStatus sts) {
        return sts != null ? itemAssetDetailEntity.operSts.eq(sts) : null;
    }

    private BooleanExpression itmNoEq(String itmNo) {
        return StringUtils.hasText(itmNo) ? itemAssetDetailEntity.itmNo.eq(itmNo) : null;
    }
}