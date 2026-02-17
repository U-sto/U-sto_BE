package com.usto.api.ai.chat.presentation.controller;

import com.usto.api.ai.chat.application.AiChatApplication;
import com.usto.api.ai.chat.application.ChatGptTestApplication;
import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

//연동 테스트
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ChatController {

    private final AiChatApplication aiChatApplication;
    private final ChatGptTestApplication chatGptTestApplication;

    @Operation(
            summary = "Chat GPT와 대화하기 테스트(AI 연동 전)",
            description = "별도의 연동 없이 그냥 지피티라 대화합니다."
    )
    @PostMapping("/gpt-connect/test")
    public ApiResponse<AiChatResponse> testChat(
            @Parameter(description = "메시지")
            @RequestParam(required = false) String message
    ) {
        AiChatResponse response = chatGptTestApplication.testSend(message);

        return ApiResponse.ok("채팅 성공",response);
    }

    @Operation(
            summary = "AI팀의 챗봇과 대화(AI 연동 후)",
            description = "AI팀의 챗봇과 대화를 진행합니다."
    )
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

    @Operation(
            summary = "AI팀의 챗봇과 대화 테스트(AI 연동 후)",
            description = "서비스로직 없이 연결 테스트만 진행합니다."
    )
    @PostMapping("/chat/test")
    public ApiResponse<AiChatResponse> chatTest(
            @RequestBody AiChatRequest request
    ) {

        AiChatResponse response = aiChatApplication.testSend(
                request.message(),
                request.threadId()
        );

        return ApiResponse.ok("채팅 성공",response);
    }
}
