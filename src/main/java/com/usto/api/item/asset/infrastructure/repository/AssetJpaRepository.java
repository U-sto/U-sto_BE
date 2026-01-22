package com.usto.api.item.asset.infrastructure.repository;

import com.usto.api.item.asset.infrastructure.entity.ItemAssetDetailEntity;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AssetJpaRepository extends JpaRepository<ItemAssetDetailEntity, ItemAssetDetailId> {

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
}