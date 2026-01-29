package com.usto.api.g2b.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.g2b.application.G2bSyncApplication;
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

    @Operation(
            summary = "G2B 목록정보 최신화(자동)"
    )
    @PutMapping("/sync")
    public ApiResponse<?> sync() {
        LocalDate now = LocalDate.now();
        String begin = now.minusDays(2).format(DateTimeFormatter.BASIC_ISO_DATE);
        String end = now.minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);

        long counts = g2bSyncApplication.syncLatest(begin,end);

        if(counts == 0){
            ApiResponse.fail("이미 동기화 된 상태입니다.");
        }
        return ApiResponse.ok("G2B물품정보 동기화 완료 ! 총"+counts+"건 변경됐습니다.");
    }
}
