package com.usto.api.ai.chat.presentation.controller;

import com.usto.api.ai.chat.application.AiChatApplication;
import com.usto.api.ai.chat.application.ChatGptTestApplication;
import com.usto.api.ai.chat.presentation.dto.request.AiChatRequest;
import com.usto.api.ai.chat.presentation.dto.response.AiChatResponse;
import com.usto.api.ai.chat.presentation.dto.response.ChatMessageResponse;
import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.domain.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

//연동 테스트
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ChatController {

    private final AiChatApplication aiChatApplication;
    private final ChatGptTestApplication chatGptTestApplication;

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
                userPrincipal.getOrgCd(),
                request.message(),
                request.threadId()
        );

        return ApiResponse.ok("채팅 성공",response);
    }

    @Operation(
            summary = "쓰레드 조회",
            description = "채팅방을 조회합니다."
    )
    @GetMapping("/chat/threads")
    public ApiResponse<List<UUID>> threads(
            @Valid @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<UUID> response = aiChatApplication.threads(
                userPrincipal.getUsername()
        );
        return ApiResponse.ok("조회 성공",response);
    }

    @Operation(
            summary = "쓰레드 삭제",
            description = "채팅방을 삭제합니다."
    )
    @DeleteMapping("/chat/threads/{threadId}")
    public ApiResponse<?> threads(
            @PathVariable UUID threadId,
            @Valid @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        aiChatApplication.deleteThread(
                threadId,
                userPrincipal.getUsername()
        );
        return ApiResponse.ok("삭제 성공");
    }

    @Operation(
            summary = "대화 맥락 조회",
            description = "채팅방 입장시 필요한 이전 대화 맥락을 조회합니다."
    )
    @GetMapping("/chat/threads/{threadId}/message")
    public ApiResponse<List<ChatMessageResponse>> findForStart(
            @PathVariable UUID threadId,
            @Valid @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<ChatMessageResponse> response = aiChatApplication.findMessage(
                threadId,
                userPrincipal.getUsername()
        );

        return ApiResponse.ok("조회 성공",response);
    }

    @Operation(
            summary = "전체 대화내용 조회",
            description = "전체에서 대화내용을 조회합니다."
    )
    @GetMapping("/chat/threads/{content}")
    public ApiResponse<List<String>> findContent(
            @PathVariable String content,
            @Valid @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<String> response = aiChatApplication.findContent(
                content,
                userPrincipal.getUsername()
        );
        return ApiResponse.ok("조회 성공",response);
    }

    @Operation(
            summary = "Chat GPT와 대화하기 테스트(AI팀 연동 X)",
            description = "별도의 연동 없이 일반 지피티랑 대화합니다."
    )
    @PostMapping("/chat/gpt4-mini")
    public ApiResponse<AiChatResponse> testChat(
            @Parameter(description = "메시지")
            @RequestParam(required = false) String message
    ) {
        AiChatResponse response = chatGptTestApplication.testSend(message);

        return ApiResponse.ok("채팅 성공",response);
    }
}
