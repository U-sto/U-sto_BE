package com.usto.api.g2b.infrastructure.repository;

import com.usto.api.g2b.infrastructure.entity.G2bSyncHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface G2bSyncHistoryJpaRepository extends JpaRepository<G2bSyncHistoryJpaEntity, Long> {
    G2bSyncHistoryJpaEntity save(G2bSyncHistoryJpaEntity history);
}
