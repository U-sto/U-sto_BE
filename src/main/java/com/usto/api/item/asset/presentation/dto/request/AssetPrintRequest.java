package com.usto.api.item.asset.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "물품 QR 라벨 출력 요청")
public class AssetPrintRequest {

    @NotEmpty(message = "물품고유번호 목록은 필수입니다.")
    @Schema(description = "물품고유번호 목록", example = "[\"M202600001\", \"M202600002\"]")
    private List<String> itmNos;
}
