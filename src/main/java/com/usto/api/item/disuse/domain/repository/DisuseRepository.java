package com.usto.api.item.disuse.domain.repository;

import com.usto.api.item.disuse.domain.model.DisuseDetail;
import com.usto.api.item.disuse.domain.model.DisuseMaster;
import com.usto.api.item.disuse.presentation.dto.request.DisuseSearchRequest;
import com.usto.api.item.disuse.presentation.dto.response.DisuseItemListResponse;
import com.usto.api.item.disuse.presentation.dto.response.DisuseListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface DisuseRepository {
    DisuseMaster saveMaster(DisuseMaster master);
    void saveDetail(DisuseDetail detail);
    Optional<DisuseMaster> findMasterById(UUID dsuMId, String orgCd);

    // 불용등록목록 조회
    Page<DisuseListResponse> findAllByFilter(DisuseSearchRequest cond, String orgCd, Pageable pageable);
    // 불용물품목록 조회
    Page<DisuseItemListResponse> findItemsByMasterId(UUID dsuMId, String orgCd, Pageable pageable);

    void deleteMaster(UUID dsuMId);
    void deleteAllDetailsByMasterId(UUID dsuMId);

    // 물품 고유번호 목록 조회
    List<String> findItemNosByMasterId(UUID dsuMId, String orgCd);

    // 중복 체크 (IN 쿼리)
    List<String> findDuplicatedItems(List<String> itmNos, UUID excludeDsuMId, String orgCd);
    // Batch 저장
    void saveAllDetails(List<DisuseDetail> details);

    List<DisuseDetail> findDetailsByMasterId(UUID dsuMId, String orgCd);

    // 물품번호 리스트로 승인된 불용 마스터 정보(상태, 사유) 조회
    Map<String, DisuseMaster> findApprovedMastersByItmNos(List<String> itmNos, String orgCd);
}