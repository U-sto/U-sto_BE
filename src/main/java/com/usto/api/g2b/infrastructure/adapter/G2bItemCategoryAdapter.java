package com.usto.api.g2b.infrastructure.adapter;

import com.usto.api.g2b.domain.service.G2bItemCategoryService;
import com.usto.api.g2b.infrastructure.entity.G2bItemCategoryJpaEntity;
import com.usto.api.g2b.infrastructure.repository.G2bItemCategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class G2bItemCategoryAdapter implements G2bItemCategoryService {

    private final G2bItemCategoryJpaRepository g2bItemCategoryJpaRepository;

    @Override
    public int updateMaster() {
        return g2bItemCategoryJpaRepository.updateMaster();
    }

    @Override
    public List<G2bItemCategoryJpaEntity> findByFilters(
            String code,
            String name
    ){
        return g2bItemCategoryJpaRepository.findByFilters(code,name);
    }
}

