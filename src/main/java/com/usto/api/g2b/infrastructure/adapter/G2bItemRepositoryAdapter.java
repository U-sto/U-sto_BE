package com.usto.api.g2b.infrastructure.adapter;
import com.usto.api.g2b.domain.repository.G2bItemRepository;
import com.usto.api.g2b.infrastructure.repository.G2bItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class G2bItemRepositoryAdapter implements G2bItemRepository {

    private final G2bItemJpaRepository jpaItemjpaRepository;

    @Override
    public int updateItems(String actor) {
        return jpaItemjpaRepository.updateItems(actor);
    }

    @Override
    public int insertItems(String actor) {
        return jpaItemjpaRepository.insertItems(actor);
    }

}
