package com.usto.api.item.disuse.infrastructure.repository;

import com.usto.api.item.disuse.infrastructure.entity.ItemDisuseDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DisuseDetailJpaRepository
        extends JpaRepository<ItemDisuseDetailEntity, UUID> {
    @Query("SELECT d.itmNo FROM ItemDisuseDetailEntity d WHERE d.dsuMId = :dsuMId AND d.orgCd = :orgCd")
    List<String> findItemNosByDsuMIdAndOrgCd(@Param("dsuMId") UUID dsuMId, @Param("orgCd") String orgCd);

    @Modifying
    @Query("UPDATE ItemDisuseDetailEntity d SET d.delYn = 'Y', d.delAt = CURRENT_TIMESTAMP WHERE d.dsuMId = :dsuMId")
    void deleteAllByDsuMId(@Param("dsuMId") UUID dsuMId);
}