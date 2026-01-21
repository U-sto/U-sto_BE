package com.usto.api.g2b.infrastructure.repository;

import com.usto.api.g2b.infrastructure.entity.G2bStgJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface G2bStgJpaRepository extends JpaRepository<G2bStgJpaEntity, String> {

    //STG테이블을 사용하기 전에 한번 싹 ~ 비워주는 메서드
    @Modifying
    @Query(value =
            "TRUNCATE TABLE TB_G2B_STG",
            nativeQuery = true)
    void truncate();
}
