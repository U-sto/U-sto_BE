package com.usto.api.g2b.infrastructure.mapper;

import com.usto.api.g2b.domain.model.G2bSyncHistory;
import com.usto.api.g2b.infrastructure.entity.G2bSyncHistoryJpaEntity;

public class G2bSyncHistoryMapper {

    //domain -> entity
    public static G2bSyncHistoryJpaEntity toEntity(G2bSyncHistory d) {
        return G2bSyncHistoryJpaEntity.builder()
                .stsYn(d.stsYn())
                .begin(d.begin())
                .end(d.end())
                .fetched(d.fetched())
                .deduped(d.deduped())
                .duplicated(d.duplicated())
                .insertedCategory(d.insertedCategory())
                .insertedItem(d.insertedItem())
                .updatedCategory(d.updatedCategory())
                .updatedItem(d.updatedItem())
                .changed(d.changed())
                .errCd(d.errCd())
                .creBy(d.creBy())
                .creAt(d.creAt())
                .build();
    }

    //entity -> domain
    public static G2bSyncHistory toDomain(G2bSyncHistoryJpaEntity e) {
        return new G2bSyncHistory(
                e.getSyncHisId(),
                Boolean.TRUE.equals(e.getStsYn()),
                e.getBegin(),
                e.getEnd(),
                e.getFetched(),
                e.getDeduped(),
                e.getDuplicated(),
                e.getInsertedCategory(),
                e.getInsertedItem(),
                e.getUpdatedCategory(),
                e.getUpdatedItem(),
                e.getChanged(),
                e.getErrCd(),
                e.getCreBy(),
                e.getCreAt()
        );
    }
}
