package com.usto.api.item.asset.domain.repository;

import com.usto.api.item.asset.domain.model.Asset;
import com.usto.api.item.asset.domain.model.AssetStatusHistory;
import com.usto.api.item.asset.presentation.dto.request.AssetListForPrintRequest;
import com.usto.api.item.asset.presentation.dto.request.AssetSearchRequest;
import com.usto.api.item.asset.presentation.dto.response.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository {

    // Asset CRUD
    Asset save(Asset asset);
    Optional<Asset> findById(String itmNo, String orgCd);
    List<Asset> findAllById(List<String> itmNos, String orgCd);
    void delete(String itmNo, String orgCd);

    // Asset 조회
    Page<AssetListResponse> findAllByFilter(AssetSearchRequest cond, String orgCd, Pageable pageable);
    Optional<AssetDetailResponse> findDetailById(String itmNo, String orgCd);

    // 상태 이력 조회
    void saveStatusHistory(AssetStatusHistory history);
    List<AssetDetailResponse.StatusHistoryDto> findStatusHistoriesByItmNo(String itmNo, String orgCd);    // 상태 이력 저장

    // 중복 생성 방지용 존재 확인
    boolean existsAssetByAcqId(UUID acqId);

    // 물품번호 순번 조회
    int getNextSequenceForYear(int year, String orgCd);

    // AI 조회용
    List<AssetAiItemDetailResponse> findAllByG2bCode(String g2bMCd, String g2bDCd, String orgCd);

    List<AssetAiItemDetailResponse> findAllByG2bName(String g2bDNm, String orgCd);

    List<AssetAiItemDetailResponse> findAllByG2bDCd(String g2bDCd, String orgCd);

    List<AssetAiItemDetailResponse> findAllByG2bMCd(String g2bMCd, String orgCd);

    List<AssetAiItemDetailResponse> findOneByItmNo(String itmNo, String orgCd);

    Asset findAssetById(String itmNo, String orgCd);

    void saveAll(List<Asset> assetsToUpdate);

    AssetPublicDetailResponse findPublicDetailByItmNoAndOrgCd(String itmNo, String orgCd);

    Page<AssetListForPrintResponse> findAllByFilterForPrint(@Valid AssetListForPrintRequest searchRequest, String orgCd, Pageable pageable);

    void bulkDisposal(List<Asset> assets, String userId, String orgCd);

    void bulkSoftDelete(List<Asset> assets, String userId, String orgCd);

    void bulkDisuse(List<Asset> assets, String userId, String orgCd);

    void bulkReturning(List<Asset> assets,String userId,String orgCd);
}