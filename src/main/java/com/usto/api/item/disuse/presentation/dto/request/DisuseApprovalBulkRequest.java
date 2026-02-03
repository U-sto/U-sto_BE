package com.usto.api.item.disuse.presentation.dto.request;

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
@Schema(description = "불용 승인 확정 요청")
public class DisuseApprovalBulkRequest {

    @Schema(description = "불용 ID 목록", example = "[\"uuid1\", \"uuid2\"]") //체크박스로 가져옴
    @NotEmpty(message = "불용 ID는 최소 1개 이상이어야 합니다.")
    private List<UUID> dsuMIds;
}
