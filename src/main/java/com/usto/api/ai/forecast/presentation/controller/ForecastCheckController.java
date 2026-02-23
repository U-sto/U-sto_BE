package com.usto.api.ai.forecast.presentation.controller;

import com.usto.api.ai.forecast.application.ForecastApplication;
import com.usto.api.ai.forecast.presentation.dto.response.AiForecastResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/api/test/ai")
@RequiredArgsConstructor
public class ForecastCheckController {

    private final ForecastApplication forecastApplication;

    @Operation(
            summary = "기록 확인",
            description = "이전 통계 그래프 자료를 봅니다."
    )
    @GetMapping
    public String check(
            @RequestParam @Valid UUID forecastId,
            @Valid @AuthenticationPrincipal UserPrincipal userPrincipal,
            Model model
    ) {
        AiForecastResponse data = forecastApplication.check(
                userPrincipal.getUsername(),
                userPrincipal.getOrgCd(),
                forecastId
        );

        model.addAttribute("data", data); // Thymeleaf에서 사용할 이름

        return "check";
    }
}
