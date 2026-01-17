package com.usto.api.item.acquisition.domain.repository;

import com.usto.api.item.acquisition.infrastructure.entity.ItemAcquisitionEntity;
import com.usto.api.item.acquisition.presentation.dto.request.AcqSearchRequest;
import com.usto.api.item.acquisition.presentation.dto.response.AcqListResponse;

import java.util.List;
import java.util.Optional;

public interface AcquisitionRepository {
    ItemAcquisitionEntity save(ItemAcquisitionEntity entity);
    Optional<ItemAcquisitionEntity> findById(Long id);
    List<AcqListResponse> findAllByFilter(AcqSearchRequest cond, String orgCd);
    void delete(ItemAcquisitionEntity entity);
}