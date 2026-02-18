// G2bItemJpaRepository.java
package com.usto.api.g2b.infrastructure.repository;

import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @class G2bItemJpaRepository
 * @desc 세부 품목 조회를 위한 레포지토리
 */
public interface G2bItemJpaRepository extends JpaRepository<G2bItemJpaEntity, String> {

    // 분류코드, 식별코드, 품목명 중 입력된 값에 대해서만 AND 검색 수행
    @Query("SELECT i FROM G2bItemJpaEntity i " +
            "WHERE (:mCd IS NULL OR :mCd = '' OR i.g2bMCd = :mCd) " + // 품목 조회 시 물품분류코드는 사용자 입력이 아니므로 완전 일치 방식
            "AND (:dCd IS NULL OR :dCd = '' OR i.g2bDCd LIKE CONCAT(:dCd, '%')) " + // 코드 검색은 전방 일치
            "AND (:dNm IS NULL OR :dNm = '' OR i.g2bDNm LIKE CONCAT('%', :dNm, '%'))")
    Page<G2bItemJpaEntity> findByFilters(
            @Param("mCd") String mCd,
            @Param("dCd") String dCd,
            @Param("dNm") String dNm,
            Pageable pageable);

    //G2B정보 업데이트
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    INSERT INTO TB_G2B001D (G2B_D_CD, G2B_M_CD, G2B_D_NM, G2B_UPR, CRE_BY)
    SELECT S.G2B_D_CD, S.G2B_M_CD, S.G2B_D_NM, S.G2B_UPR, :actor
    FROM TB_G2B_STG S
    LEFT JOIN TB_G2B001D D ON D.G2B_D_CD = S.G2B_D_CD
    WHERE D.G2B_D_CD IS NULL
    """, nativeQuery = true)
    int insertItems(String actor);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    UPDATE TB_G2B001D D
    JOIN TB_G2B_STG S ON S.G2B_D_CD = D.G2B_D_CD
    SET D.G2B_M_CD = S.G2B_M_CD,
        D.G2B_D_NM = S.G2B_D_NM,
        D.G2B_UPR  = S.G2B_UPR,
        D.UPD_BY   = :actor
    WHERE (D.G2B_M_CD <> S.G2B_M_CD
        OR D.G2B_D_NM <> S.G2B_D_NM
        OR D.G2B_UPR  <> S.G2B_UPR)
    """, nativeQuery = true)
    int updateItems(String actor);
}