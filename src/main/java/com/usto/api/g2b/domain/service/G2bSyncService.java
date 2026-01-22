package com.usto.api.g2b.domain.service;

import com.usto.api.g2b.domain.model.G2bSync;

import java.util.List;

public interface G2bSyncService {
    //비워놓고
    void truncate();
    //새로 채우고
    void bulkInsert(List<G2bSync> rows);
}
