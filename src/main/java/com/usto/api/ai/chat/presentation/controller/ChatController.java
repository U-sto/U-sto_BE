package com.usto.api.ai.chat.presentation.controller;

import com.usto.api.ai.chat.application.AiChatApplication;
import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.ai.chat.presentation.dto.response.VendorChatResponse;
import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//연동 테스트
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ChatController {

    private final AiChatApplication aiChatApplication;

    @PostMapping("/chat")
    public ApiResponse<AiChatResponse> chat(
            @RequestBody AiChatRequest request,
            @Valid @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {

        AiChatResponse response = aiChatApplication.send(
                userPrincipal.getUsername(),
                request.message(),
                request.threadId()
        );

        return ApiResponse.ok("채팅 성공",response);
    }
}
