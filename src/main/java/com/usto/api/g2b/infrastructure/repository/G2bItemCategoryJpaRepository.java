package com.usto.api.g2b.infrastructure.repository;

import com.usto.api.g2b.infrastructure.entity.G2bItemCategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @class G2bItemCategoryJpaRepository
 * @desc 물품 분류 조회를 위한 레포지토리
 */
public interface G2bItemCategoryJpaRepository extends JpaRepository<G2bItemCategoryJpaEntity, String> {

    @Query("SELECT c FROM G2bItemCategoryJpaEntity c " +
            "WHERE (:code IS NULL OR :code = '' OR c.g2bMCd LIKE :code%) " + // 코드 검색은 전방 일치
            "AND (:name IS NULL OR :name = '' OR c.g2bMNm LIKE %:name%)")
    List<G2bItemCategoryJpaEntity> findByFilters(
            @Param("code") String code,
            @Param("name") String name);
}