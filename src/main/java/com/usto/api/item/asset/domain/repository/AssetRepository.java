package com.usto.api.item.asset.domain.repository;

import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetMaster;
import com.usto.api.item.asset.domain.model.AssetStatusHistory;
import com.usto.api.item.asset.presentation.dto.request.AssetSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.AssetDetailResponse;
import com.usto.api.item.asset.presentation.dto.response.AssetListResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository {
    Asset save(Asset asset);
    void saveMaster(AssetMaster master);
    Optional<Asset> findById(String itmNo, String orgCd);
    List<AssetListResponse> findAllByFilter(AssetSearchRequest cond, String orgCd);

    // 개별 물품 상세 조회
    Optional<AssetDetailResponse> findDetailById(String itmNo, String orgCd);
    // 상태 이력 조회
    List<AssetDetailResponse.StatusHistoryDto> findStatusHistoriesByItmNo(String itmNo, String orgCd);    // 상태 이력 저장
    void saveStatusHistory(AssetStatusHistory history);

    // 중복 생성 방지용 존재 확인
    boolean existsMasterByAcqId(UUID acqId);
    // 물품번호 순번 조회
    int getNextSequenceForYear(int year, String orgCd);
    void delete(String itmNo, String orgCd);

    List<AssetMaster> findAllById(List<UUID> acqIds);

    void saveAll(List<AssetMaster> assetMasters);
}