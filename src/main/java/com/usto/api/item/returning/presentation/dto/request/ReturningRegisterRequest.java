package com.usto.api.item.returning.presentation.dto.request;

import com.usto.api.item.common.model.ItemStatus;
import com.usto.api.item.returning.domain.model.ReturningReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "반납 신청 등록 요청")
public class ReturningRegisterRequest {

    @Schema(description = "반납일자", example = "2026-01-28")
    @NotNull(message = "반납일자는 필수입니다.")
    @PastOrPresent(message = "반납일자는 미래 날짜를 선택할 수 없습니다.")
    private LocalDate aplyAt;

    @Schema(description = "물품상태 (NEW:신품, USED:중고품, REPAIRABLE:요정비품, SCRAP:폐품)", example = "USED")
    @NotNull(message = "물품상태는 필수입니다.")
    private ItemStatus itemSts;

    @Schema(description = "반납사유 (USAGE_PERIOD_EXPIRED:사용연한경과, BROKEN:고장/파손, DISUSE_DECISION:불용결정, PROJECT_ENDED:사업종료, SURPLUS:잉여물품", example = "BROKEN")
    @NotNull(message = "반납사유는 필수입니다.")
    private ReturningReason rtrnRsn;

    @Schema(description = "반납할 물품 고유번호 목록", example = "[\"M202600001\", \"M202600002\"]")
    @NotEmpty(message = "반납할 물품을 최소 1개 이상 선택해야 합니다.")
    private List<String> itmNos;
}