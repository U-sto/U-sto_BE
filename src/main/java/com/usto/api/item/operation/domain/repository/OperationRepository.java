package com.usto.api.item.operation.domain.repository;

import com.usto.api.item.operation.domain.model.OperationDetail;
import com.usto.api.item.operation.domain.model.OperationMaster;
import com.usto.api.item.operation.presentation.dto.request.OperationSearchRequest;
import com.usto.api.item.operation.presentation.dto.response.OperationItemListResponse;
import com.usto.api.item.operation.presentation.dto.response.OperationListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperationRepository {
    OperationMaster saveMaster(OperationMaster master);
    void saveDetail(OperationDetail detail);
    Optional<OperationMaster> findMasterById(UUID operMId, String orgCd);

    // 등록목록 조회
    Page<OperationListResponse> findAllByFilter(OperationSearchRequest cond, String orgCd, Pageable pageable);
    // 물품목록 조회
    Page<OperationItemListResponse> findItemsByMasterId(UUID operMId, String orgCd, Pageable pageable);

    void deleteMaster(UUID operMId);
    void deleteAllDetailsByMasterId(UUID operMId);

    // 물품 고유번호 목록 조회
    List<String> findItemNosByMasterId(UUID operMId, String orgCd);

    List<String> findDuplicatedItems(List<String> itmNos, UUID excludeOperMId, String orgCd);
    // Batch 저장
    void saveAllDetails(List<OperationDetail> details);

    List<OperationDetail> findDetailsByMasterId(UUID operMId, String orgCd);
}