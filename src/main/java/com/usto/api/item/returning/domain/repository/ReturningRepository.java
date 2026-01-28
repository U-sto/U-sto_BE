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

    boolean existsPendingReturnDetail(String itmNo, String orgCd);
    void deleteMaster(UUID rtrnMId);
    List<String> findItemNosByMasterId(UUID rtrnMId, String orgCd);
}