package com.usto.api.g2b.infrastructure.adapter;

import com.usto.api.g2b.domain.model.StgPriceRow;
import com.usto.api.g2b.domain.service.G2bStgService;
import com.usto.api.g2b.infrastructure.repository.G2bStgJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class G2bStgAdapter implements G2bStgService {

    private final G2bStgJpaRepository jpaRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void truncate() {
        jpaRepository.truncate();
    }

    @Override
    public void bulkInsert(List<StgPriceRow> rows) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO TB_G2B_STG (G2B_D_CD, G2B_UPR) VALUES (?, ?)",
                rows,
                5000,
                (ps, r) -> {
                    ps.setString(1, r.getG2bDCd());
                    ps.setLong(2, r.getG2bUpr());
                }
        );
    }
}
