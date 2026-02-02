package com.usto.api.g2b.infrastructure.adapter;

import com.usto.api.g2b.domain.model.G2bUsrfulList;
import com.usto.api.g2b.domain.repository.G2bUsrfulListRepository;
import com.usto.api.g2b.infrastructure.repository.G2bUsrfulListJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class G2bUsrfulListRepositoryAdapter implements G2bUsrfulListRepository {

    private final JdbcTemplate jdbcTemplate;
    private final G2bUsrfulListJpaRepository g2bUsrfulListJpaRepository;


    @Override
    public void bulkInsert(List<G2bUsrfulList> domainList) {
        if (domainList == null || domainList.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(
                "INSERT INTO TB_G2B_USRFUL " +
                        "(G2B_M_CD, G2B_M_NM, DRB_YR) " +
                        "VALUES (?, ?, ?) "
                ,
                domainList,
                100,
                (ps, r) -> {
                    ps.setString(1, r.getG2bMcd());
                    ps.setString(2, r.getG2bMNm());
                    ps.setString(3, r.getDrbYr());
                }
        );
    }

    @Override
    public void delete() {
        g2bUsrfulListJpaRepository.delete();
    }

    @Override
    public String findDrbYrByCode(String code) {
        return g2bUsrfulListJpaRepository.findDrbYrByCode(code);
    }

    @Override
    public String findNameByCode(String code) {
        return g2bUsrfulListJpaRepository.findNameByCode(code);
    }
}
