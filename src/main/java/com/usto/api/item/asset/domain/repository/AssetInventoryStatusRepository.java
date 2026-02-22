package com.usto.api.item.asset.domain.repository;

import com.usto.api.item.asset.presentation.dto.request.AssetInventoryStatusSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetInventoryStatusListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetInventoryStatusRepository {
    Page<AssetInventoryStatusListResponse> findAllByFilter(
            AssetInventoryStatusSearchRequest cond, String orgCd, Pageable pageable);
}
