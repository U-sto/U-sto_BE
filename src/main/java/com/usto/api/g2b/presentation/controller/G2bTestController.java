package com.usto.api.g2b.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.g2b.application.G2bTestApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "g2b-controller", description = "G2B API")
@RestController
@RequestMapping("/api/g2b")
@RequiredArgsConstructor
public class G2bTestController {

    private final G2bTestApplication g2bTestApplication;

    @Operation(
            summary = "공공데이터 포털 연동 테스트"
    )
    @GetMapping("/test")
    public ApiResponse<?> test(
            @Parameter(description = "페이지번호")
            @RequestParam(required = false) String pageNo ,
            @Parameter(description = "한 페이지 결과 수 ")
            @RequestParam(required = false) String numOfRows,
            @Parameter(description = "조회구분 ")
            @RequestParam(required = false) String inqryDiv,
            @Parameter(description = "조회기준시작일자 ")
            @RequestParam(required = false) String inqryBgnDate,
            @Parameter(description = "조회기준종료일자 ")
            @RequestParam(required = false) String inqryEndDate

    ) {

        if (inqryBgnDate != null && inqryBgnDate.length() != 8) inqryBgnDate = null;
        if (inqryEndDate != null && inqryEndDate.length() != 8) inqryEndDate = null;

        var result = g2bTestApplication.test(
                pageNo,
                numOfRows,
                inqryDiv,
                inqryBgnDate,
                inqryEndDate);

        return ApiResponse.ok("테스트 완료",result);
    }
}
