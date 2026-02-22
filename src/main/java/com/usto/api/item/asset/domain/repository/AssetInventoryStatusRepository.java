package com.usto.api.item.asset.domain.repository;

import com.usto.api.item.asset.presentation.dto.request.AssetInventoryStatusSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetInventoryStatusDetailResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetInventoryStatusListResponse;
import com.usto.api.item.common.model.OperStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface AssetInventoryStatusRepository {
    Page<AssetInventoryStatusListResponse> findAllByFilter(
            AssetInventoryStatusSearchRequest cond, String orgCd, Pageable pageable);

    AssetInventoryStatusDetailResponse findDetailByGroup(
            UUID acqId, String deptCd, OperStatus operSts,
            BigDecimal acqUpr, String drbYr, String rmk, String orgCd);
}
