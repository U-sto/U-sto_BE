package com.usto.api.item.asset.domain.repository;

import com.usto.api.item.asset.domain.model.AssetStatusHistory;

import java.util.List;

public interface AssetStatusHistoryRepository {

    void saveAll(List<AssetStatusHistory> histories);
}
