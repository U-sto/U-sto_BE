package com.usto.api.item.returning.domain.repository;

import com.usto.api.item.returning.domain.model.ReturningDetail;
import com.usto.api.item.returning.domain.model.ReturningMaster;
import com.usto.api.item.returning.presentation.dto.request.ReturningSearchRequest;
import com.usto.api.item.returning.presentation.dto.response.ReturningItemListResponse;
import com.usto.api.item.returning.presentation.dto.response.ReturningListResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReturningRepository {
    ReturningMaster saveMaster(ReturningMaster master);
    void saveDetail(ReturningDetail detail);
    Optional<ReturningMaster> findMasterById(UUID rtrnMId, String orgCd);
    // 반납등록목록 조회
    List<ReturningListResponse> findAllByFilter(ReturningSearchRequest cond, String orgCd);
    // 반납물품목록 조회
    List<ReturningItemListResponse> findItemsByMasterId(UUID rtrnMId, String orgCd);

    void deleteMaster(UUID rtrnMId);

    // 마스터 ID에 묶인 모든 상세 내역 삭제
    void deleteAllDetailsByMasterId(UUID rtrnMId);

    // 물품 고유번호 목록 조회
    List<String> findItemNosByMasterId(UUID rtrnMId, String orgCd);

    boolean existsInOtherReturning(String itmNo, UUID excludeRtrnMId, String orgCd);
}