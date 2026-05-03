package com.usto.api.g2b.domain.repository;


import com.usto.api.g2b.domain.model.G2bItemCategory;

import java.util.List;
import java.util.Map;

public interface G2bItemCategoryRepository {

    int insertCategory(String actor);
    int updateCategory(String actor);

    List<String> findDistinctCategoryCodes();

    int updateDrbYrIfDifferent(String prdctClsfcNo, String drbYr);

    String findDistinctCategoryNameByCode(String prdctClsfcNo);

    List<G2bItemCategory> findAll();

    void updateAll(List<G2bItemCategory> categoryDomainList);

    String findDrbYrByDetailCode(String g2bDCd);
    Map<String, String> findDrbYrMapByCodes(List<String> g2bDCds);
}