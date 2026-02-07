package com.usto.api.item.disposal.domain.repository;

import com.usto.api.item.disposal.domain.model.DisposalDetail;
import com.usto.api.item.disposal.domain.model.DisposalMaster;
import com.usto.api.item.disposal.presentation.dto.request.DisposalSearchRequest;
import com.usto.api.item.disposal.presentation.dto.response.DisposalItemListResponse;
import com.usto.api.item.disposal.presentation.dto.response.DisposalListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisposalRepository {
    DisposalMaster saveMaster(DisposalMaster master);
    void saveDetail(DisposalDetail detail);
    Optional<DisposalMaster> findMasterById(UUID dispMId, String orgCd);

    // 처분등록목록 조회
    Page<DisposalListResponse> findAllByFilter(DisposalSearchRequest cond, String orgCd, Pageable pageable);

    // 처분물품목록 조회
    Page<DisposalItemListResponse> findItemsByMasterId(UUID dispMId, String orgCd, Pageable pageable);

    void deleteMaster(UUID dispMId);
    void deleteAllDetailsByMasterId(UUID dispMId);

    // 물품 고유번호 목록 조회
    List<String> findItemNosByMasterId(UUID dispMId, String orgCd);

    // 중복 체크 (IN 쿼리)
    List<String> findDuplicatedItems(List<String> itmNos, UUID excludeDispMId, String orgCd);

    // Batch 저장
    void saveAllDetails(List<DisposalDetail> details);

    List<DisposalDetail> findDetailsByMasterId(UUID dispMId, String orgCd);
}