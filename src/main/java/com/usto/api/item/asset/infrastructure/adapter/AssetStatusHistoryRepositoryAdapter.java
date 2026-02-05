package com.usto.api.item.asset.infrastructure.adapter;

import com.usto.api.item.asset.domain.model.AssetStatusHistory;
import com.usto.api.item.asset.domain.repository.AssetStatusHistoryRepository;
import com.usto.api.item.asset.infrastructure.entity.ItemAssetStatusHistoryEntity;
import com.usto.api.item.asset.infrastructure.mapper.AssetMapper;
import com.usto.api.item.asset.infrastructure.repository.AssetStatusHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AssetStatusHistoryRepository 구현체
 */
@Component
@RequiredArgsConstructor
public class AssetStatusHistoryRepositoryAdapter implements AssetStatusHistoryRepository {

    private final AssetStatusHistoryJpaRepository jpaRepository;

    @Override
    public void saveAll(List<AssetStatusHistory> histories) {
        List<ItemAssetStatusHistoryEntity> entities = histories
                .stream()
                .map(AssetMapper::toStatusHistoryEntity) // 매핑 메서드 필요
                .toList();
        jpaRepository.saveAll(entities);
    }
}
