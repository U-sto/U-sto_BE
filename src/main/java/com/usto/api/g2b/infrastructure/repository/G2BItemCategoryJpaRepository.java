package com.usto.api.g2b.infrastructure.repository;

import com.usto.api.g2b.infrastructure.entity.G2BItemCategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @class G2BItemCategoryJpaRepository
 * @desc 물품 분류 조회를 위한 레포지토리
 */
public interface G2BItemCategoryJpaRepository extends JpaRepository<G2BItemCategoryJpaEntity, String> {

    @Query("SELECT c FROM G2BItemCategoryJpaEntity c " +
            "WHERE c.g2bMCd LIKE %:code% " +
            "AND c.g2bMNm LIKE %:name%")
    List<G2BItemCategoryJpaEntity> findByFilters(
            @Param("code") String code,
            @Param("name") String name);
}