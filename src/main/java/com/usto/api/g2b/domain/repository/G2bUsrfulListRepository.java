package com.usto.api.g2b.domain.repository;

import com.usto.api.g2b.domain.model.G2bUsrfulList;

import java.util.List;

public interface G2bUsrfulListRepository {
    void bulkInsert(List<G2bUsrfulList> domainList);

    void delete();

    String findDrbYrByCode(String code);

    String findNameByCode(String code);
}
