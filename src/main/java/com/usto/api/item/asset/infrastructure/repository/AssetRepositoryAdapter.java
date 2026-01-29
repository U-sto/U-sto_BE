package com.usto.api.item.asset.infrastructure.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usto.api.g2b.infrastructure.entity.QG2bItemCategoryJpaEntity;
import com.usto.api.g2b.infrastructure.entity.QG2bItemJpaEntity;
import com.usto.api.item.acquisition.domain.model.Acquisition;
import com.usto.api.item.acquisition.infrastructure.entity.QItemAcquisitionEntity;
import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetDetailRow;
import com.usto.api.item.asset.domain.model.AssetMaster;
import com.usto.api.item.asset.domain.model.AssetStatusHistory;
import com.usto.api.item.asset.domain.repository.AssetRepository;
import com.usto.api.item.asset.infrastructure.entity.*;
import com.usto.api.item.asset.infrastructure.mapper.AssetMapper;
import com.usto.api.item.asset.presentation.dto.request.AssetSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetAiItemDetailResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetDetailResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetListResponse;
import com.usto.api.item.common.model.OperStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


import static com.usto.api.item.asset.infrastructure.entity.QItemAssetDetailEntity.itemAssetDetailEntity;
import static com.usto.api.item.asset.infrastructure.entity.QItemAssetMasterEntity.itemAssetMasterEntity;
import static com.usto.api.g2b.infrastructure.entity.QG2bItemJpaEntity.g2bItemJpaEntity;
import static com.usto.api.organization.infrastructure.entity.QDepartmentJpaEntity.departmentJpaEntity;

@Component
@RequiredArgsConstructor
public class AssetRepositoryAdapter implements AssetRepository {

    private final AssetJpaRepository jpaRepository;
    private final AssetMasterJpaRepository jpaMasterRepository;
    private final AssetStatusHistoryJpaRepository statusHistoryJpaRepository;
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
    public Optional<Asset> findById(String itmNo, String orgCd) {
        return jpaRepository.findById(new ItemAssetDetailId(itmNo, orgCd)).map(AssetMapper::toDomain);
    }

    /**
     * 필터 조건에 따른 운용대장목록 검색
     *
     * @param cond  검색 조건 (DTO)
     * @param orgCd 현재 로그인한 유저의 조직코드
     */
    @Override
    public List<AssetListResponse> findAllByFilter(AssetSearchRequest cond, String orgCd) {
        return queryFactory
                .select(Projections.fields(AssetListResponse.class,
                        itemAssetDetailEntity.itemId.itmNo,
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
                        itemAssetDetailEntity.itemId.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemAssetDetailEntity.deptCd.eq(departmentJpaEntity.id.deptCd)
                )
                .where(
                        itemAssetDetailEntity.itemId.orgCd.eq(orgCd),
                        g2bDCdEq(cond.getG2bDCd()),
                        acqAtBetween(cond.getStartAcqAt(), cond.getEndAcqAt()),
                        arrgAtBetween(cond.getStartArrgAt(), cond.getEndArrgAt()),
                        deptCdEq(cond.getDeptCd()),
                        operStsEq(cond.getOperSts()),
                        itmNoEq(cond.getItmNo())
                )
                .orderBy(itemAssetDetailEntity.itemId.itmNo.desc())
                .fetch();
    }

    @Override
    public Optional<AssetDetailResponse> findDetailById(String itmNo, String orgCd) {
        AssetDetailResponse detail = queryFactory
                .select(Projections.fields(AssetDetailResponse.class,
                        itemAssetDetailEntity.itemId.itmNo,
                        g2bItemJpaEntity.g2bDNm.as("g2bDNm"),
                        Expressions.stringTemplate("CONCAT({0}, '-', {1})",
                                g2bItemJpaEntity.g2bMCd,
                                itemAssetDetailEntity.g2bDCd).as("g2bItemNo"),
                        itemAssetMasterEntity.acqAt,
                        itemAssetMasterEntity.arrgAt,
                        itemAssetDetailEntity.operSts.stringValue().as("operSts"),
                        itemAssetDetailEntity.drbYr,
                        itemAssetDetailEntity.acqUpr,
                        itemAssetMasterEntity.qty,
                        itemAssetMasterEntity.acqArrgTy.stringValue().as("acqArrgTy"),
                        departmentJpaEntity.deptNm.as("deptNm"),
                        itemAssetDetailEntity.deptCd,
                        itemAssetDetailEntity.rmk
                ))
                .from(itemAssetDetailEntity)
                .leftJoin(itemAssetMasterEntity).on(
                        itemAssetDetailEntity.acqId.eq(itemAssetMasterEntity.acqId)
                )
                .leftJoin(g2bItemJpaEntity).on(
                        itemAssetDetailEntity.g2bDCd.eq(g2bItemJpaEntity.g2bDCd)
                )
                .leftJoin(departmentJpaEntity).on(
                        itemAssetDetailEntity.itemId.orgCd.eq(departmentJpaEntity.id.orgCd),
                        itemAssetDetailEntity.deptCd.eq(departmentJpaEntity.id.deptCd)
                )
                .where(
                        itemAssetDetailEntity.itemId.itmNo.eq(itmNo),
                        itemAssetDetailEntity.itemId.orgCd.eq(orgCd)
                )
                .fetchOne();

        return Optional.ofNullable(detail);
    }

    @Override
    public List<AssetDetailResponse.StatusHistoryDto> findStatusHistoriesByItmNo(String itmNo, String orgCd) {
        List<ItemAssetStatusHistoryEntity> entities =
                statusHistoryJpaRepository.findByItmNoAndOrgCdOrderByApprAtDesc(itmNo, orgCd);

        return entities.stream()
                .map(entity -> AssetDetailResponse.StatusHistoryDto.builder()
                        .itemHisId(entity.getItemHisId().toString())
                        .itmNo(entity.getItmNo())
                        .prevSts(entity.getPrevSts().name())
                        .newSts(entity.getNewSts().name())
                        .chgRsn(entity.getChgRsn())
                        .reqUsrId(entity.getReqUsrId())
                        .reqAt(entity.getReqAt())
                        .apprUsrId(entity.getApprUsrId())
                        .apprAt(entity.getApprAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void saveStatusHistory(AssetStatusHistory history) {
        ItemAssetStatusHistoryEntity entity = AssetMapper.toStatusHistoryEntity(history);
        statusHistoryJpaRepository.save(entity);
    }

    /**
     * 자산 중복 생성 방지용 검사 메서드
     */
    @Override
    public boolean existsMasterByAcqId(UUID acqId) {
        return queryFactory
                .selectOne()
                .from(itemAssetMasterEntity)
                .where(itemAssetMasterEntity.acqId.eq(acqId))
                .fetchFirst() != null;
    }

    /**
     * 신규 물품번호 생성을 위한 다음 순번 조회
     * - JpaRepository의 Native Query를 호출하여 DB의 MAX값 조회
     * TODO: DB 레벨에서 원자적으로 순번을 발급하도록 개선 필요
     */
    @Override
    public int getNextSequenceForYear(int year, String orgCd) {
        int maxSequence = jpaRepository.findMaxSequenceByYear(String.valueOf(year), orgCd);
        return maxSequence + 1;
    }

    @Override
    public void delete(String itmNo, String orgCd) {
        jpaRepository.deleteById(new ItemAssetDetailId(itmNo, orgCd));
    }

    @Override
    public List<AssetMaster> findAllById(List<UUID> acqIds) {
        // 1. DB에서 엔티티 리스트 조회 (List<ItemAssetMasterEntity>)
        List<ItemAssetMasterEntity> entities = jpaMasterRepository.findAllById(acqIds);

        // 2. [필수] 엔티티 -> 도메인으로 변환하여 반환
        return entities.stream()
                .map(AssetMapper::toMasterDomain) // Entity를 Domain으로 바꾸는 매퍼 필요
                .toList();
    }

    @Override
    public List<AssetAiItemDetailResponse> findAllByG2bCode(String g2bMCd, String g2bDCd, String orgCd){
        return jpaRepository.findAllByG2bCode(g2bMCd, g2bDCd, orgCd);
    }

    @Override
    public List<AssetAiItemDetailResponse> findAllByG2bName(String g2bDNm, String orgCd){
        return jpaRepository.findAllByG2bName(g2bDNm, orgCd);
    }

    @Override
    public List<AssetAiItemDetailResponse> findAllByG2bDCd(String g2bDCd, String orgCd){
        return jpaRepository.findAllByG2bDCd(g2bDCd, orgCd);
    }

    @Override
    public List<AssetAiItemDetailResponse> findAllByG2bMCd(String g2bMCd, String orgCd){
        return jpaRepository.findAllByG2bMCd(g2bMCd, orgCd);
    }

    @Override
    public List<AssetAiItemDetailResponse> findOneByItmNo(String itmNo, String orgCd) {
        return jpaRepository.findOneByItmNo(itmNo, orgCd);
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
        return StringUtils.hasText(itmNo) ? itemAssetDetailEntity.itemId.itmNo.eq(itmNo) : null;
    }


}