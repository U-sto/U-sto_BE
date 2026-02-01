package com.usto.api.g2b.infrastructure.adapter;

import com.usto.api.g2b.domain.model.G2bStg;
import com.usto.api.g2b.domain.repository.G2bStgRepository;
import com.usto.api.g2b.infrastructure.mapper.G2bStgMapper;
import com.usto.api.g2b.infrastructure.repository.G2bStgJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class G2bStgRepositoryAdapter implements G2bStgRepository {

    private final G2bStgJpaRepository g2bStgJpaRepository;
    private final JdbcTemplate jdbcTemplate;
/*
    @Override
    public void truncate() {
        g2bStgJpaRepository.truncate();
    }
*/

    @Override
    public void bulkInsert(List<G2bStg> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(
                "INSERT IGNORE INTO TB_G2B_STG " +  //중복 에러를 무시하고 진행
                        "(G2B_M_CD, G2B_M_NM, G2B_D_CD, G2B_D_NM, G2B_UPR) " +
                        "VALUES (?, ?, ?, ?, ?)",
                rows,
                100,
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

    @Override
    public List<String> findDistinctCategoryCodes() {
        return g2bStgJpaRepository.findDistinctCategoryCodes();
    }

    @Override
    public int updateDrbYrIfDifferent(String prdctClsfcNo, String drbYr) {
        return updateDrbYrIfDifferent(prdctClsfcNo, drbYr);
    }

    @Override
    public void delete(){
        g2bStgJpaRepository.delete();
    };
    @Override
    public void resetId(){
        g2bStgJpaRepository.resetId();
    };
}
