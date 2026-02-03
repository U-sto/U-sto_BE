package com.usto.api.item.asset.infrastructure.repository;

import com.usto.api.item.asset.infrastructure.entity.ItemAssetDetailEntity;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetDetailId;
import com.usto.api.item.asset.presentation.dto.response.AssetAiItemDetailResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetListResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetPublicDetailResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface AssetJpaRepository extends JpaRepository<ItemAssetDetailEntity, ItemAssetDetailId> {

    @Query("""
    SELECT NEW com.usto.api.item.asset.presentation.dto.response.AssetAiItemDetailResponse(
        d.itemId.itmNo,
        g.g2bDNm,
        c.g2bMCd,
        g.g2bDCd,
        m.acqAt,
        m.arrgAt,
        d.operSts,
        d.drbYr,
        d.acqUpr,
        m.qty,
        a.acqQty,
        a.arrgTy,
        d.deptCd,
        d.rmk
    )
    FROM ItemAssetDetailEntity d
        JOIN ItemAssetMasterEntity m
            ON m.acqId = d.acqId
        JOIN ItemAcquisitionEntity a
            ON a.acqId = d.acqId
        JOIN G2bItemJpaEntity g
            ON g.g2bDCd = d.g2bDCd
        JOIN G2bItemCategoryJpaEntity c
            ON c.g2bMCd = g.g2bMCd
    WHERE d.itemId.orgCd = :orgCd
      AND d.delYn = 'N'
      AND m.delYn = 'N'
      AND a.delYn = 'N'
      AND c.g2bMCd = :g2bMCd
      AND g.g2bDCd = :g2bDCd
    ORDER BY m.acqAt DESC, d.itemId.itmNo ASC
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
        m.acqAt,
        m.arrgAt,
        d.operSts,
        d.drbYr,
        d.acqUpr,
        m.qty,
        a.acqQty,
        a.arrgTy,
        d.deptCd,
        d.rmk
    )
    FROM ItemAssetDetailEntity d
        JOIN ItemAssetMasterEntity m ON m.acqId = d.acqId
        JOIN ItemAcquisitionEntity a ON a.acqId = d.acqId
        JOIN G2bItemJpaEntity g ON g.g2bDCd = d.g2bDCd
        JOIN G2bItemCategoryJpaEntity c ON c.g2bMCd = g.g2bMCd
    WHERE d.itemId.orgCd = :orgCd
      AND d.delYn = 'N'
      AND m.delYn = 'N'
      AND a.delYn = 'N'
      AND g.g2bDNm LIKE CONCAT('%', :g2bDNm, '%')
    ORDER BY m.acqAt DESC, d.itemId.itmNo ASC
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
        m.acqAt,
        m.arrgAt,
        d.operSts,
        d.drbYr,
        d.acqUpr,
        m.qty,
        a.acqQty,
        a.arrgTy,
        d.deptCd,
        d.rmk
    )
    FROM ItemAssetDetailEntity d
        JOIN ItemAssetMasterEntity m ON m.acqId = d.acqId
        JOIN ItemAcquisitionEntity a ON a.acqId = d.acqId
        JOIN G2bItemJpaEntity g ON g.g2bDCd = d.g2bDCd
        JOIN G2bItemCategoryJpaEntity c ON c.g2bMCd = g.g2bMCd
    WHERE d.itemId.orgCd = :orgCd
      AND d.delYn = 'N'
      AND m.delYn = 'N'
      AND a.delYn = 'N'
      AND g.g2bDCd = :g2bDCd
    ORDER BY m.acqAt DESC, d.itemId.itmNo ASC
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
        m.acqAt,
        m.arrgAt,
        d.operSts,
        d.drbYr,
        d.acqUpr,
        m.qty,
        a.acqQty,
        a.arrgTy,
        d.deptCd,
        d.rmk
    )
    FROM ItemAssetDetailEntity d
        JOIN ItemAssetMasterEntity m ON m.acqId = d.acqId
        JOIN ItemAcquisitionEntity a ON a.acqId = d.acqId
        JOIN G2bItemJpaEntity g ON g.g2bDCd = d.g2bDCd
        JOIN G2bItemCategoryJpaEntity c ON c.g2bMCd = g.g2bMCd
    WHERE d.itemId.orgCd = :orgCd
      AND d.delYn = 'N'
      AND m.delYn = 'N'
      AND a.delYn = 'N'
      AND c.g2bMCd = :g2bMCd
    ORDER BY m.acqAt DESC, d.itemId.itmNo ASC
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
        m.acqAt,
        m.arrgAt,
        d.operSts,
        d.drbYr,
        d.acqUpr,
        m.qty,
        a.acqQty,
        a.arrgTy,
        d.deptCd,
        d.rmk
    )
    FROM ItemAssetDetailEntity d
        JOIN ItemAssetMasterEntity m ON m.acqId = d.acqId
        JOIN ItemAcquisitionEntity a ON a.acqId = d.acqId
        JOIN G2bItemJpaEntity g ON g.g2bDCd = d.g2bDCd
        JOIN G2bItemCategoryJpaEntity c ON c.g2bMCd = g.g2bMCd
    WHERE d.itemId.itmNo = :itmNo
      AND d.itemId.orgCd = :orgCd
      AND d.delYn = 'N'
      AND m.delYn = 'N'
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
    FROM TB_ITEM002D
    WHERE SUBSTRING(ITM_NO, 2, 4) = :year
      AND ORG_CD = :orgCd
    """, nativeQuery = true)
    int findMaxSequenceByYear(@Param("year") String year, @Param("orgCd") String orgCd);

    @Query(value = """
        SELECT 
            d.ITM_NO as itmNo,
            d.ORG_CD as orgCd,
            g.G2B_D_NM as g2bDNm,
            CONCAT(c.G2B_M_CD, '-', g.G2B_D_CD) as g2bItemNo,
            d.ACQ_UPR as acqUpr,
            m.ACQ_AT as acqAt,
            m.ARRG_AT as arrgAt,
            d.OPER_STS as operSts,
            d.DRB_YR as drbYr,
            dept.DEPT_NM as deptNm,
            m.QTY as qty,
            d.RMK as rmk
        FROM TB_ITEM002D d
        INNER JOIN TB_ITEM002M m 
            ON m.ACQ_ID = d.ACQ_ID 
            AND m.DEL_YN = 'N'
        INNER JOIN TB_G2B001D g 
            ON g.G2B_D_CD = d.G2B_D_CD
        INNER JOIN TB_G2B001M c 
            ON c.G2B_M_CD = g.G2B_M_CD
        INNER JOIN TB_ORG002M dept 
            ON dept.DEPT_CD = d.DEPT_CD 
            AND dept.ORG_CD = d.ORG_CD
        WHERE d.ITM_NO = :itmNo
          AND d.ORG_CD = :orgCd
          AND d.DEL_YN = 'N'
          AND m.DEL_YN = 'N'
        """, nativeQuery = true)
    AssetPublicDetailResponse findPublicDetailByItmNoAndOrgCd(
            @Param("itmNo") String itmNo,
            @Param("orgCd") String orgCd
    );
}