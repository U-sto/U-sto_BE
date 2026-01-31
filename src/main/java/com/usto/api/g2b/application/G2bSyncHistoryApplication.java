package com.usto.api.g2b.application;

import com.usto.api.g2b.domain.model.G2bSyncHistory;
import com.usto.api.g2b.domain.model.SyncResult;
import com.usto.api.g2b.domain.repository.G2bSyncHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class G2bSyncHistoryApplication {

    private final G2bSyncHistoryRepository g2bSyncHistoryRepository;

    //result -> domain
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void success(SyncResult result, String actor) {
        g2bSyncHistoryRepository.save(G2bSyncHistory.success(result, actor));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fail(String begin, String end, String actor, String errCd) {
        g2bSyncHistoryRepository.save(G2bSyncHistory.fail(begin, end, actor, errCd));
    }
}
