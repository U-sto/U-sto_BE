package com.usto.api.g2b.domain.repository;


import java.util.List;

public interface G2bItemCategoryRepository {

    int insertCategory(String actor);
    int updateCategory(String actor);

    List<String> findDistinctCategoryCodes();

    int updateDrbYrIfDifferent(String prdctClsfcNo, String drbYr);
}