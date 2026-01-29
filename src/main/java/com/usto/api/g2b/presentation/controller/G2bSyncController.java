package com.usto.api.g2b.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.g2b.application.G2bSyncApplication;
import com.usto.api.g2b.application.G2bSyncHistoryApplication;
import com.usto.api.g2b.domain.model.SyncResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Tag(name = "g2b-controller", description = "G2B API")
@RestController
@RequestMapping("/api/g2b")
@RequiredArgsConstructor
public class G2bSyncController {

    private final G2bSyncApplication g2bSyncApplication;
    private final G2bSyncHistoryApplication g2bSyncHistoryApplication;

    @Operation(
            summary = "G2B 목록정보 최신화(자동)"
    )
    @PutMapping("/sync")
    public ApiResponse<?> sync() {
        LocalDate now = LocalDate.now();
        String begin = now.minusDays(2).format(DateTimeFormatter.BASIC_ISO_DATE);
        String end = now.minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

        SyncResult result = g2bSyncApplication.syncLatest(begin,end);

        if(result.changed() == 0){
            ApiResponse.fail("동기화 결과, 변경사항이 없습니다..");
            g2bSyncHistoryApplication.fail(begin,end,"SYSTEM","200");
        }

        g2bSyncHistoryApplication.success(result,"SYSTEM");

        String msg = String.format(
                "G2B 물품정보 동기화 완료. (기간 %s~%s) " +
                        "수집 %d건 → 적재 %d건(중복 %d건 제거). " +
                        "카테고리 신규 %d건 / 수정 %d건, 품목 신규 %d건 / 수정 %d건. " +
                        "총 변경 %d건.",
                result.begin(), result.end(),
                result.fetched(), result.deduped(), result.duplicated(),
                result.insertedCategory(), result.updatedCategory(),
                result.insertedItem(), result.updatedItem(),
                result.changed()
        );

        return ApiResponse.ok(msg);
    }
}
