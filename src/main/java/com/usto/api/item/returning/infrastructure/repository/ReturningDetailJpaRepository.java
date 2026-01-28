package com.usto.api.item.returning.infrastructure.repository;

import com.usto.api.item.returning.infrastructure.entity.ItemReturningDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReturningDetailJpaRepository extends JpaRepository<ItemReturningDetailEntity, UUID> {

    @Query("SELECT d.itmNo FROM ItemReturningDetailEntity d WHERE d.rtrnMId = :rtrnMId AND d.orgCd = :orgCd")
    List<String> findItemNosByRtrnMIdAndOrgCd(@Param("rtrnMId") UUID rtrnMId, @Param("orgCd") String orgCd);
}