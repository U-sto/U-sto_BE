package com.usto.api.item.asset.infrastructure.repository;

import com.usto.api.item.asset.infrastructure.entity.ItemAssetDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssetJpaRepository extends JpaRepository<ItemAssetDetailEntity, String> {

    // TODO: 이게 여기 구현되는게 맞나?? 위치가
    /**
     * 특정 연도의 마지막 순번 조회 (물품번호 생성용)
     * 예: M2026XXXXX 형식에서 XXXXX 부분의 최대값
     */
    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(itm_no, 6, 5) AS UNSIGNED)), 0) " +
            "FROM TB_ITEM002D " +
            "WHERE itm_no LIKE CONCAT('M', :year, '%') " +
            "AND org_cd = :orgCd " +
            "AND del_yn = 'N'",
            nativeQuery = true)
    int findMaxSequenceByYear(@Param("year") int year, @Param("orgCd") String orgCd);
}