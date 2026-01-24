package com.usto.api.g2b.infrastructure.adapter;

import com.usto.api.g2b.domain.model.G2bStg;
import com.usto.api.g2b.domain.service.G2bStgService;
import com.usto.api.g2b.infrastructure.entity.G2bStgMapper;
import com.usto.api.g2b.infrastructure.repository.G2bStgJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class G2bStgAdapter implements G2bStgService {

    private final G2bStgJpaRepository g2bStgJpaRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void truncate() {
        g2bStgJpaRepository.truncate();
    }

    @Override
    public void bulkInsert(List<G2bStg> rows) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO TB_G2B_STG (G2B_M_CD, G2B_M_NM, G2B_D_CD, G2B_D_NM, G2B_UPR) " +
                        "VALUES (?, ?, ?, ?, ?)",
                rows,
                5000,
                (ps, r) -> {
                    ps.setString(1, r.getG2bMCd());
                    ps.setString(2, r.getG2bMNm());
                    ps.setString(3, r.getG2bDCd());
                    ps.setString(4, r.getG2bDNm());
                    ps.setLong(5, r.getG2bUpr());
                }
        );
    }

    @Override
    public long countChanged() {
        return g2bStgJpaRepository.countChanged();
    }
    
    @Override
    public List<G2bStg> findAll() {
        return g2bStgJpaRepository.findAll().stream()
                .map(G2bStgMapper::toDomain)
                .toList();
    }
}
