package com.usto.api.g2b.domain.repository;

import com.usto.api.g2b.domain.model.G2bStg;

import java.util.List;

public interface G2bStgRepository {
    //비워놓고
    //void truncate();

    void delete();
    void resetId();
    //새로 채우고
    void bulkInsert(List<G2bStg> rows);
    //변경 건 세기
    long countChanged();

    List<G2bStg> findAll();

    List<String> findDistinctCategoryCodes();

    int updateDrbYrIfDifferent(String prdctClsfcNo, String drbYr);
}
