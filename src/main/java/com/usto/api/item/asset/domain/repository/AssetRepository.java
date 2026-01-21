package com.usto.api.item.asset.domain.repository;

import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetMaster;
import com.usto.api.item.asset.presentation.dto.request.AssetSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetListResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository {
    Asset save(Asset asset);
    void saveMaster(AssetMaster master);
    Optional<Asset> findById(String itmNo);
    List<AssetListResponse> findAllByFilter(AssetSearchRequest cond, String orgCd);
    int getNextSequenceForYear(int year, String orgCd);  // 물품번호 순번 조회
    void delete(String itmNo);
}