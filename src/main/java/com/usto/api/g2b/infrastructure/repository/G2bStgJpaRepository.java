package com.usto.api.g2b.infrastructure.repository;

import com.usto.api.g2b.domain.model.G2bStg;
import com.usto.api.g2b.infrastructure.entity.G2bStgJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface G2bStgJpaRepository extends JpaRepository<G2bStgJpaEntity,Long> {

    @Modifying
    @Query(value = "TRUNCATE TABLE TB_G2B_STG", nativeQuery = true)
    void truncate();

    @Query("""
      SELECT COUNT(DISTINCT D.g2bDCd)
      FROM G2bStgJpaEntity S
      JOIN G2bItemJpaEntity D ON D.g2bDCd = S.g2bDCd
      JOIN G2bItemCategoryJpaEntity M ON M.g2bMCd = D.g2bMCd
      WHERE
           COALESCE(TRIM(S.g2bMCd), '') <> COALESCE(TRIM(D.g2bMCd), '')
        OR COALESCE(TRIM(S.g2bMNm), '') <> COALESCE(TRIM(M.g2bMNm), '')
        OR COALESCE(TRIM(S.g2bDCd), '') <> COALESCE(TRIM(D.g2bDCd), '')
        OR COALESCE(TRIM(S.g2bDNm), '') <> COALESCE(TRIM(D.g2bDNm), '')
        OR COALESCE(S.g2bUpr, 0) <> COALESCE(D.g2bUpr, 0)
    """)
    long countChanged();
}