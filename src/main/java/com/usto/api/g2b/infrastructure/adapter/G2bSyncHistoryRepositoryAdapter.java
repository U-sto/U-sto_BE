package com.usto.api.g2b.infrastructure.adapter;

import com.usto.api.g2b.domain.model.G2bSyncHistory;
import com.usto.api.g2b.domain.repository.G2bSyncHistoryRepository;
import com.usto.api.g2b.infrastructure.mapper.G2bSyncHistoryMapper;
import com.usto.api.g2b.infrastructure.repository.G2bSyncHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class G2bSyncHistoryRepositoryAdapter implements G2bSyncHistoryRepository {

    private final G2bSyncHistoryJpaRepository g2bSyncHistoryJpaRepository;

    @Override
    public G2bSyncHistory save(G2bSyncHistory history) {
        var entity = G2bSyncHistoryMapper.toEntity(history);
        var saved = g2bSyncHistoryJpaRepository.save(entity);
        return G2bSyncHistoryMapper.toDomain(saved);
    }
}
