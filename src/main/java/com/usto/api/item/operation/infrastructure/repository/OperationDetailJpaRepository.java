package com.usto.api.item.operation.infrastructure.repository;

import com.usto.api.item.operation.infrastructure.entity.ItemOperationDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OperationDetailJpaRepository extends JpaRepository<ItemOperationDetailEntity, UUID> {

    @Query("SELECT d.itmNo FROM ItemOperationDetailEntity d WHERE d.operMId = :operMId AND d.orgCd = :orgCd")
    List<String> findItemNosByOperMIdAndOrgCd(@Param("operMId") UUID operMId, @Param("orgCd") String orgCd);

    @Modifying
    @Query("UPDATE ItemOperationDetailEntity d SET d.delYn = 'Y', d.delAt = CURRENT_TIMESTAMP WHERE d.operMId = :operMId")
    void deleteAllByOperMId(@Param("operMId") UUID operMId);
}