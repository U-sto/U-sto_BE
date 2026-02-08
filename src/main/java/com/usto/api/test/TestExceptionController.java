package com.usto.api.test;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "test-exception-controller", description = "예외 테스트 API")
@Slf4j
@RestController
@RequestMapping("/api/test/exception")
public class TestExceptionController {

    @GetMapping("/business")
    @Operation(summary = "BusinessException 발생 테스트")
    public ApiResponse<?> throwBusinessException(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("🧪 [테스트] 인증된 사용자: {}", principal.getUsername());
        throw new BusinessException("의도적인 BusinessException 발생!");
    }

    @GetMapping("/runtime")
    @Operation(summary = "RuntimeException 발생 테스트")
    public ApiResponse<?> throwRuntimeException(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("🧪 [테스트] 인증된 사용자: {}", principal.getUsername());
        throw new RuntimeException("의도적인 RuntimeException 발생!");
    }

    @GetMapping("/nullpointer")
    @Operation(summary = "NullPointerException 발생 테스트")
    public ApiResponse<?> throwNullPointerException(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("🧪 [테스트] 인증된 사용자: {}", principal.getUsername());
        String nullString = null;
        return ApiResponse.ok("NullPointError 발생 ",nullString.length());
    }

    @GetMapping("/divide-by-zero")
    @Operation(summary = "ArithmeticException 발생 테스트")
    public ApiResponse<?> throwArithmeticException(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("🧪 [테스트] 인증된 사용자: {}", principal.getUsername());
        int result = 10 / 0;
        return ApiResponse.ok("ArithmeticException 발생",result);
    }

    @GetMapping("/array-index")
    @Operation(summary = "ArrayIndexOutOfBoundsException 발생 테스트")
    public ApiResponse<?> throwArrayIndexException(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("🧪 [테스트] 인증된 사용자: {}", principal.getUsername());
        int[] arr = {1, 2, 3};
        return ApiResponse.ok("배열 범위 초과",arr[10]);
    }

    @GetMapping("/check-auth")
    @Operation(summary = "인증 상태 확인 (예외 없음)")
    public ApiResponse<?> checkAuth(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("✅ [정상] 인증된 사용자: {}", principal.getUsername());
        return ApiResponse.ok("인증 상태 정상", principal.getUsername());
    }
}