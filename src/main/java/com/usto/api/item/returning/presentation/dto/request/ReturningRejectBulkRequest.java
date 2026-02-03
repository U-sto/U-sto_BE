package com.usto.api.item.returning.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "반납 승인 반려 요청")
public class ReturningRejectBulkRequest {

    @Schema(description = "반납 ID 목록", example = "[\"uuid1\", \"uuid2\"]")
    @NotEmpty(message = "반납 ID는 최소 1개 이상이어야 합니다.")
    private List<UUID> rtrnMIds;
}
