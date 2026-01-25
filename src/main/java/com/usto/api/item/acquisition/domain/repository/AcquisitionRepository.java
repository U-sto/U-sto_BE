package com.usto.api.item.acquisition.domain.repository;

import com.usto.api.item.acquisition.domain.model.Acquisition;
import com.usto.api.item.acquisition.presentation.dto.request.AcqSearchRequest;
import com.usto.api.item.acquisition.presentation.dto.response.AcqListResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AcquisitionRepository {
    // Domain Model 사용
    Acquisition save(Acquisition acquisition);
    Optional<Acquisition> findById(UUID id);
    void delete(UUID acqId);

    // 조회는 Response DTO 직접 반환 (변경 없음)
    List<AcqListResponse> findAllByFilter(AcqSearchRequest cond, String orgCd);

    List<Acquisition> findAllById(List<UUID> acqIds);

    void saveAll(List<Acquisition> acquisitions);
}