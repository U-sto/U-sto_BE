package com.usto.api.g2b.domain.service;

import com.usto.api.g2b.domain.model.StgPriceRow;

import java.util.List;

public interface G2bStgService {
    //비워놓고
    void truncate();
    //새로 채우고
    void bulkInsert(List<StgPriceRow> rows);
}
