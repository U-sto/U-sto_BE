package com.usto.api.item.acquisition.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class AcqApprovalBulkRequestDto {
    @NotEmpty(message = "승인할 항목을 최소 1개 이상 선택해야 합니다.")
    private List<UUID> acqIds;
}
