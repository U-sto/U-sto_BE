package com.usto.api.g2b.infrastructure.repository;

import com.usto.api.g2b.infrastructure.entity.G2bUsrfulListJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface G2bUsrfulListJpaRepository extends JpaRepository<G2bUsrfulListJpaEntity, String> {

    @Modifying
    @Query(value =
            "DELETE FROM TB_G2B_USRFUL"
            , nativeQuery = true)
    void delete();

    @Query(value = """
        SELECT DRB_YR 
        FROM TB_G2B_USRFUL M
        WHERE M.G2B_M_CD = :code        
        """, nativeQuery = true)
    String findDrbYrByCode(String code);

    @Query(value = """
        SELECT G2B_M_NM
        FROM TB_G2B_USRFUL M
        WHERE M.G2B_M_CD = :code        
        """, nativeQuery = true)
    String findNameByCode(String code);
}
