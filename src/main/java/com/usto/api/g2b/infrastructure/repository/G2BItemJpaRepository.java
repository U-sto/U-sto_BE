package com.usto.api.g2b.infrastructure.repository;

import com.usto.api.g2b.infrastructure.entity.G2BItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * @class G2BItemJpaRepository
 * @desc 세부 품목 조회를 위한 레포지토리
 */
public interface G2BItemJpaRepository extends JpaRepository<G2BItemJpaEntity, String> {
    List<G2BItemJpaEntity> findByG2bMCdAndG2bDCdContainingAndG2bDNmContaining(
            String g2bMCd, String g2bDCd, String g2bDNm);
}