package com.usto.api.item.returning.domain.repository;

import com.usto.api.item.returning.domain.model.ReturningDetail;
import com.usto.api.item.returning.domain.model.ReturningMaster;
import com.usto.api.item.returning.presentation.dto.request.ReturningSearchRequest;
import com.usto.api.item.returning.presentation.dto.response.ReturningItemListResponse;
import com.usto.api.item.returning.presentation.dto.response.ReturningListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReturningRepository {
    ReturningMaster saveMaster(ReturningMaster master);
    void saveDetail(ReturningDetail detail);
    Optional<ReturningMaster> findMasterById(UUID rtrnMId, String orgCd);
    // 반납등록목록 조회
    Page<ReturningListResponse> findAllByFilter(ReturningSearchRequest cond, String orgCd, Pageable pageable);
    // 반납물품목록 조회
    Page<ReturningItemListResponse> findItemsByMasterId(UUID rtrnMId, String orgCd, Pageable pageable);

    void deleteMaster(UUID rtrnMId);

    // 마스터 ID에 묶인 모든 상세 내역 삭제
    void deleteAllDetailsByMasterId(UUID rtrnMId);

    // 물품 고유번호 목록 조회
    List<String> findItemNosByMasterId(UUID rtrnMId, String orgCd);

    boolean existsInOtherReturning(String itmNo, UUID excludeRtrnMId, String orgCd);

    List<ReturningDetail> findDetailsByMasterId(UUID rtrnMId, String orgCd);
}