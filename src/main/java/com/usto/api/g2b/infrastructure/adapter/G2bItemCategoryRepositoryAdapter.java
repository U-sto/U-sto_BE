package com.usto.api.g2b.infrastructure.adapter;


import com.usto.api.g2b.domain.model.G2bItemCategory;
import com.usto.api.g2b.domain.repository.G2bItemCategoryRepository;
import com.usto.api.g2b.infrastructure.entity.G2bItemCategoryJpaEntity;
import com.usto.api.g2b.infrastructure.mapper.G2bItemCategoryMapper;
import com.usto.api.g2b.infrastructure.repository.G2bItemCategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class G2bItemCategoryRepositoryAdapter implements G2bItemCategoryRepository {

    private final G2bItemCategoryJpaRepository g2bItemCategoryJpaRepository;
    private final JdbcTemplate jdbcTemplate;

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

    @Override
    public String findDistinctCategoryNameByCode(String prdctClsfcNo) {
        return g2bItemCategoryJpaRepository.findDistinctCategoryNameByCode(prdctClsfcNo);
    }

    @Override
    public List<G2bItemCategory> findAll() {
        String sql = "SELECT * FROM TB_G2B001M";

        return jdbcTemplate.query(sql,
                (rs,
                 rowNum) ->
                G2bItemCategory.builder()
                        .g2bMCd(rs.getString("G2B_M_CD"))
                        .g2bMNm(rs.getString("G2B_M_NM"))
                        .drbYr(rs.getString("DRB_YR"))
                        .build()
        );
    }

    @Override
    public void updateAll(List<G2bItemCategory> domains) {
        if (domains == null || domains.isEmpty()) {
            return;
        }

        // JPA saveAll 대신 JDBC로 직접 UPDATE 쿼리 실행
        // 이유: JPA는 Detached Entity의 변경 감지가 불확실할 수 있음
        String sql =
                "UPDATE TB_G2B001M " +
                "SET DRB_YR = ?, " + "UPD_AT = NOW() " +
                "WHERE G2B_M_CD = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                G2bItemCategory item = domains.get(i);

                // 1. 바꿀 값 (내용연수)
                ps.setString(1, item.getDrbYr());

                // 2. 조건 (PK - 물품분류번호)
                ps.setString(2, item.getG2bMCd());
            }

            @Override
            public int getBatchSize() {
                return domains.size();
            }
        });
        log.info("마스터 테이블(TB_G2B001M) {}건 강제 업데이트 완료", domains.size());
    }
}

