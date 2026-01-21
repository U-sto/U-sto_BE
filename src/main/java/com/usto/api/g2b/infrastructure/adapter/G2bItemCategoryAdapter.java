package com.usto.api.g2b.infrastructure.adapter;

import com.usto.api.g2b.domain.service.G2bItemCategoryService;
import com.usto.api.g2b.infrastructure.repository.G2bItemCategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class G2bItemCategoryAdapter implements G2bItemCategoryService {

    private final G2bItemCategoryJpaRepository g2bItemCategoryJpaRepository;

}
