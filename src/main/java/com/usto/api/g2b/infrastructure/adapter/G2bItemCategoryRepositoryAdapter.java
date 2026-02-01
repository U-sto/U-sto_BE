package com.usto.api.g2b.infrastructure.adapter;


import com.usto.api.g2b.domain.repository.G2bItemCategoryRepository;
import com.usto.api.g2b.infrastructure.repository.G2bItemCategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class G2bItemCategoryRepositoryAdapter implements G2bItemCategoryRepository {

    private final G2bItemCategoryJpaRepository g2bItemCategoryJpaRepository;

    @Override
    public int updateCategory(String actor) {
        return g2bItemCategoryJpaRepository.updateCategory(actor);
    }

    @Override
    public List<String> findDistinctCategoryCodes() {
        return g2bItemCategoryJpaRepository.findDistinctCategoryCodes();
    }

    @Override
    public int updateDrbYrIfDifferent(String prdctClsfcNo, String drbYr) {
        return g2bItemCategoryJpaRepository.updateDrbYrIfDifferent(prdctClsfcNo, drbYr);
    }

    @Override
    public int insertCategory(String actor) {
        return g2bItemCategoryJpaRepository.insertCategory(actor);
    }

}

