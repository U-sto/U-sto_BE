package com.usto.api.g2b.infrastructure.repository;

import com.usto.api.g2b.infrastructure.entity.G2bStgJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface G2bSyncDiffJpaRepository extends JpaRepository<G2bStgJpaEntity, Long> {

    // 분류명 변경 건수 (STG vs M)
    @Query(value = """
                SELECT COUNT(*)
                FROM TB_G2B_STG s
                JOIN TB_G2B001M m ON m.G2B_M_CD = s.G2B_M_CD
                WHERE m.G2B_M_NM <> s.G2B_M_NM
            """, nativeQuery = true)
    int countMasterNameChanges();

    // 식별명(품목명) 변경 건수 (STG vs D)
    @Query(value = """
                SELECT COUNT(*)
                FROM TB_G2B_STG s
                JOIN TB_G2B001D d ON d.G2B_D_CD = s.G2B_D_CD
                WHERE d.G2B_D_NM <> s.G2B_D_NM
            """, nativeQuery = true)
    int countDetailNameChanges();

    // 단가 변경 건수 (STG vs D)
    @Query(value = """
                SELECT COUNT(*)
                FROM TB_G2B_STG s
                JOIN TB_G2B001D d ON d.G2B_D_CD = s.G2B_D_CD
                WHERE d.G2B_UPR <> s.G2B_UPR
            """, nativeQuery = true)
    int countUprChanges();
}
