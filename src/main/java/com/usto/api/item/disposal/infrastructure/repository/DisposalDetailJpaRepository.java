package com.usto.api.item.disposal.infrastructure.repository;

import com.usto.api.item.disposal.infrastructure.entity.ItemDisposalDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DisposalDetailJpaRepository extends JpaRepository<ItemDisposalDetailEntity, UUID> {

    @Query("SELECT d.itmNo FROM ItemDisposalDetailEntity d WHERE d.dispMId = :dispMId AND d.orgCd = :orgCd")
    List<String> findItemNosByDispMIdAndOrgCd(@Param("dispMId") UUID dispMId, @Param("orgCd") String orgCd);

    @Modifying
    @Query("UPDATE ItemDisposalDetailEntity d SET d.delYn = 'Y', d.delAt = CURRENT_TIMESTAMP WHERE d.dispMId = :dispMId")
    void deleteAllByDispMId(@Param("dispMId") UUID dispMId);
}