package com.usto.api.g2b.domain.repository;

import com.usto.api.g2b.domain.model.G2bSyncHistory;

public interface G2bSyncHistoryRepository {
    G2bSyncHistory save(G2bSyncHistory history);
}
