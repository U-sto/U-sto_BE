package com.usto.api.g2b.domain.service;

import com.usto.api.g2b.domain.model.G2bStg;

import java.util.List;

public interface G2bStgService {
    //비워놓고
    void truncate();
    //새로 채우고
    void bulkInsert(List<G2bStg> rows);
    //변경 건 세기
    long countChanged();

    List<G2bStg> findAll();
}
