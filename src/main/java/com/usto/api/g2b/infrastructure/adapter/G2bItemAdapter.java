package com.usto.api.g2b.infrastructure.adapter;

import com.usto.api.g2b.domain.service.G2bItemService;
import com.usto.api.g2b.infrastructure.entity.G2bItemJpaEntity;
import com.usto.api.g2b.infrastructure.repository.G2bItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class G2bItemAdapter implements G2bItemService {

    private final G2bItemJpaRepository jpaItemjpaRepository;

    @Override
    public int updateChangedPrices() {
        return jpaItemjpaRepository.updateChangedPrices();
    }

    @Override
    public List<G2bItemJpaEntity> findByFilters
            (String mCd,
             String dCd,
             String dNm)
    {
        return jpaItemjpaRepository.findByFilters(mCd,dCd,dNm);
    }
}
