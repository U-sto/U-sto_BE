package com.usto.api.item.asset.infrastructure.repository;

import com.usto.api.item.asset.infrastructure.entity.ItemAssetEntity;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetId;
import com.usto.api.item.asset.presentation.dto.response.AssetAiItemDetailResponse;
import com.usto.api.item.common.model.OperStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssetJpaRepository extends JpaRepository<ItemAssetEntity, ItemAssetId> {
    @Query("""
    SELECT NEW com.usto.api.item.asset.presentation.dto.response.AssetAiItemDetailResponse(
        d.itemId.itmNo,
        g.g2bDNm,
        c.g2bMCd,
        g.g2bDCd,
        a.acqAt,
        a.apprAt,
        d.operSts,
        d.drbYr,
        d.acqUpr,
        (SELECT COUNT(sub) FROM ItemAssetEntity sub WHERE sub.acqId = d.acqId AND sub.delYn = 'N'),
        a.acqQty,
        a.arrgTy,
        d.deptCd,
        d.rmk
    )
    FROM ItemAssetEntity d
        JOIN ItemAcquisitionEntity a ON a.acqId = d.acqId
        JOIN G2bItemJpaEntity g ON g.g2bDCd = d.g2bDCd
        JOIN G2bItemCategoryJpaEntity c ON c.g2bMCd = g.g2bMCd
    WHERE d.itemId.orgCd = :orgCd
      AND d.delYn = 'N'
      AND a.delYn = 'N'
      AND c.g2bMCd = :g2bMCd
      AND g.g2bDCd = :g2bDCd
    ORDER BY a.acqAt DESC, d.itemId.itmNo ASC
""")
    List<AssetAiItemDetailResponse> findAllByG2bCode(
            @Param("g2bMCd") String g2bMCd,
            @Param("g2bDCd") String g2bDCd,
            @Param("orgCd") String orgCd
    );

    @Query("""
    SELECT NEW com.usto.api.item.asset.presentation.dto.response.AssetAiItemDetailResponse(
        d.itemId.itmNo,
        g.g2bDNm,
        c.g2bMCd,
        g.g2bDCd,
        a.acqAt,
        a.apprAt,
        d.operSts,
        d.drbYr,
        d.acqUpr,
        (SELECT COUNT(sub) FROM ItemAssetEntity sub WHERE sub.acqId = d.acqId AND sub.delYn = 'N'),
        a.acqQty,
        a.arrgTy,
        d.deptCd,
        d.rmk
    )
    FROM ItemAssetEntity d
        JOIN ItemAcquisitionEntity a ON a.acqId = d.acqId
        JOIN G2bItemJpaEntity g ON g.g2bDCd = d.g2bDCd
        JOIN G2bItemCategoryJpaEntity c ON c.g2bMCd = g.g2bMCd
    WHERE d.itemId.orgCd = :orgCd
      AND d.delYn = 'N'
      AND a.delYn = 'N'
      AND g.g2bDNm LIKE CONCAT('%', :g2bDNm, '%')
    ORDER BY a.acqAt DESC, d.itemId.itmNo ASC
""")
    List<AssetAiItemDetailResponse> findAllByG2bName(
            @Param("g2bDNm") String g2bDNm,
            @Param("orgCd") String orgCd
    );

    @Query("""
    SELECT NEW com.usto.api.item.asset.presentation.dto.response.AssetAiItemDetailResponse(
        d.itemId.itmNo,
        g.g2bDNm,
        c.g2bMCd,
        g.g2bDCd,
        a.acqAt,
        a.apprAt,
        d.operSts,
        d.drbYr,
        d.acqUpr,
        (SELECT COUNT(sub) FROM ItemAssetEntity sub WHERE sub.acqId = d.acqId AND sub.delYn = 'N'),
        a.acqQty,
        a.arrgTy,
        d.deptCd,
        d.rmk
    )
    FROM ItemAssetEntity d
        JOIN ItemAcquisitionEntity a ON a.acqId = d.acqId
        JOIN G2bItemJpaEntity g ON g.g2bDCd = d.g2bDCd
        JOIN G2bItemCategoryJpaEntity c ON c.g2bMCd = g.g2bMCd
    WHERE d.itemId.orgCd = :orgCd
      AND d.delYn = 'N'
      AND a.delYn = 'N'
      AND g.g2bDCd = :g2bDCd
    ORDER BY a.acqAt DESC, d.itemId.itmNo ASC
""")
    List<AssetAiItemDetailResponse> findAllByG2bDCd(
            @Param("g2bDCd") String g2bDCd,
            @Param("orgCd") String orgCd
    );

    @Query("""
    SELECT NEW com.usto.api.item.asset.presentation.dto.response.AssetAiItemDetailResponse(
        d.itemId.itmNo,
        g.g2bDNm,
        c.g2bMCd,
        g.g2bDCd,
        a.acqAt,
        a.apprAt,
        d.operSts,
        d.drbYr,
        d.acqUpr,
        (SELECT COUNT(sub) FROM ItemAssetEntity sub WHERE sub.acqId = d.acqId AND sub.delYn = 'N'),
        a.acqQty,
        a.arrgTy,
        d.deptCd,
        d.rmk
    )
    FROM ItemAssetEntity d
        JOIN ItemAcquisitionEntity a ON a.acqId = d.acqId
        JOIN G2bItemJpaEntity g ON g.g2bDCd = d.g2bDCd
        JOIN G2bItemCategoryJpaEntity c ON c.g2bMCd = g.g2bMCd
    WHERE d.itemId.orgCd = :orgCd
      AND d.delYn = 'N'
      AND a.delYn = 'N'
      AND c.g2bMCd = :g2bMCd
    ORDER BY a.acqAt DESC, d.itemId.itmNo ASC
""")
    List<AssetAiItemDetailResponse> findAllByG2bMCd(
            @Param("g2bMCd") String g2bMCd,
            @Param("orgCd") String orgCd
    );

    @Query("""
    SELECT NEW com.usto.api.item.asset.presentation.dto.response.AssetAiItemDetailResponse(
        d.itemId.itmNo,
        g.g2bDNm,
        c.g2bMCd,
        g.g2bDCd,
        a.acqAt,
        a.apprAt,
        d.operSts,
        d.drbYr,
        d.acqUpr,
        (SELECT COUNT(sub) FROM ItemAssetEntity sub WHERE sub.acqId = d.acqId AND sub.delYn = 'N'),
        a.acqQty,
        a.arrgTy,
        d.deptCd,
        d.rmk
    )
    FROM ItemAssetEntity d
        JOIN ItemAcquisitionEntity a ON a.acqId = d.acqId
        JOIN G2bItemJpaEntity g ON g.g2bDCd = d.g2bDCd
        JOIN G2bItemCategoryJpaEntity c ON c.g2bMCd = g.g2bMCd
    WHERE d.itemId.itmNo = :itmNo
      AND d.itemId.orgCd = :orgCd
      AND d.delYn = 'N'
      AND a.delYn = 'N'
""")
    List<AssetAiItemDetailResponse> findOneByItmNo(
            @Param("itmNo") String itmNo,
            @Param("orgCd") String orgCd
    );

    /**
     * 특정 연도에 한 조직에서 마지막 물품의 순번 조회 (물품번호 생성용)
     * 예: M2026XXXXX 형식에서 XXXXX 부분의 최대값
     */
    @Query(value = """
    SELECT COALESCE(
        MAX(CAST(SUBSTRING(ITM_NO, 6, 5) AS UNSIGNED)), 0)
    FROM TB_ITEM002
    WHERE SUBSTRING(ITM_NO, 2, 4) = :year
      AND ORG_CD = :orgCd
    """, nativeQuery = true)
    int findMaxSequenceByYear(@Param("year") String year, @Param("orgCd") String orgCd);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ItemAssetEntity a
           set a.operSts = :newSts,
               a.updBy   = :updBy
         where a.itemId.itmNo in :itemNos
           and a.itemId.orgCd = :orgCd
           and a.delYn = 'N'
    """)
    void bulkDisposal(
            @Param("itemNos") List<String> itemNos,
            @Param("updBy") String updBy,
            @Param("orgCd") String orgCd,
            @Param("newSts") OperStatus newSts
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update ItemAssetEntity a
           set a.delYn = 'Y',
               a.updBy = :updBy,
               a.delAt = NOW()
         where a.itemId.itmNo in :itemNos
           and a.itemId.orgCd = :orgCd
           and a.delYn = 'N'
    """)
    void bulkSoftDelete(
            @Param("itemNos") List<String> itemNos,
            @Param("updBy") String updBy,
            @Param("orgCd") String orgCd
    );

    @Modifying
    @Query("""
        update ItemAssetEntity a
           set a.operSts = :newSts,
               a.updBy   = :updBy
         where a.itemId.itmNo in :itemNos
           and a.itemId.orgCd = :orgCd
           and a.delYn = 'N'
    """)
    void bulkDisuse(
            @Param("itemNos") List<String> itemNos,
            @Param("updBy") String updBy,
            @Param("orgCd") String orgCd,
            @Param("newSts") OperStatus newSts
    );

    @Modifying
    @Query("""
        update ItemAssetEntity a
           set a.operSts = :newSts,
               a.updBy   = :updBy,
               a.deptCd = "NONE"
         where a.itemId.itmNo in :itemNos
           and a.itemId.orgCd = :orgCd
           and a.delYn = 'N'
    """)
    void bulkReturning(
            @Param("itemNos") List<String> itemNos,
            @Param("updBy") String updBy,
            @Param("orgCd") String orgCd,
            @Param("newSts") OperStatus newSts
    );
}