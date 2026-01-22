package com.usto.api.g2b.infrastructure.adapter;

import com.usto.api.g2b.domain.service.G2bSyncDiffService;
import com.usto.api.g2b.infrastructure.repository.G2bSyncDiffJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class G2bSyncDiffAdapter implements G2bSyncDiffService {

    private final G2bSyncDiffJpaRepository repo;

    @Override
    public int countMasterNameChanges() {
        return repo.countMasterNameChanges();
    }
    @Override
    public int countDetailNameChanges() {
        return repo.countDetailNameChanges();
    }
    @Override
    public int countUprChanges() {
        return repo.countUprChanges();
    }
}
