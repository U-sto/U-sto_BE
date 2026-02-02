package com.usto.api.g2b.infrastructure.repository;

import com.usto.api.g2b.domain.model.G2bItemCategory;
import com.usto.api.g2b.infrastructure.entity.G2bItemCategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @class G2bItemCategoryJpaRepository
 * @desc 물품 분류 조회를 위한 레포지토리
 */
public interface G2bItemCategoryJpaRepository extends JpaRepository<G2bItemCategoryJpaEntity, String> {

    @Query("SELECT c FROM G2bItemCategoryJpaEntity c " +
            "WHERE (:code IS NULL OR :code = '' OR c.g2bMCd LIKE CONCAT(:code, '%')) " + // 코드 검색은 전방 일치
            "AND (:name IS NULL OR :name = '' OR c.g2bMNm LIKE CONCAT('%', :name, '%'))")
    List<G2bItemCategoryJpaEntity> findByFilters(
            @Param("code") String code,
            @Param("name") String name);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    INSERT INTO TB_G2B001M (G2B_M_CD, G2B_M_NM, CRE_BY)
    SELECT DISTINCT S.G2B_M_CD, S.G2B_M_NM, :actor
    FROM TB_G2B_STG S
    LEFT JOIN TB_G2B001M M ON M.G2B_M_CD = S.G2B_M_CD
    WHERE M.G2B_M_CD IS NULL
    """, nativeQuery = true)
    int insertCategory(String actor);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    UPDATE TB_G2B001M M
    JOIN (
      SELECT S.G2B_M_CD, MAX(S.G2B_M_NM) AS G2B_M_NM
      FROM TB_G2B_STG S
      GROUP BY S.G2B_M_CD
    ) S ON S.G2B_M_CD = M.G2B_M_CD
    SET M.G2B_M_NM = S.G2B_M_NM,
        M.UPD_BY   = :actor,
        M.UPD_AT   = CURRENT_TIMESTAMP
    WHERE M.G2B_M_NM <> S.G2B_M_NM
    """, nativeQuery = true)
    int updateCategory(String actor);

    @Query(value = "SELECT DISTINCT G2B_M_CD " +
            "FROM TB_G2B001M", nativeQuery = true)
    List<String> findDistinctCategoryCodes();


    @Modifying
    @Query(value = """
    UPDATE TB_G2B001M M
        SET M.DRB_YR = :drbYr
    WHERE M.G2B_M_CD = :code
    """, nativeQuery = true)
    int updateDrbYrIfDifferent(
            String code,
            String drbYr
            );

    @Query(value =
            """
        SELECT G2B_M_NM FROM TB_G2B001M M
        WHERE M.G2B_M_CD = :code
        """
            , nativeQuery = true)
    String findDistinctCategoryNameByCode(
            String code
    );

}