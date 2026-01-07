package com.usto.api.g2b.infrastructure.repository;

import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
    List<G2bItemJpaEntity> findByFilters(
            @Param("mCd") String mCd,
            @Param("dCd") String dCd,
            @Param("dNm") String dNm);
}