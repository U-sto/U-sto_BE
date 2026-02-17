package com.usto.api.ai.chat.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import java.util.List;

@Builder
public record AiChatResponse(
        @JsonProperty("reply")
        String replyMessage,      // AI 답변 내용

        @JsonProperty("references")
        List<String> references,  // AI가 참고한 문서 리스트 (["doc1.pdf", "doc2.json"])

        @JsonProperty("created_at")
        String createdAt          // 생성 시간 (문자열로 받고 필요시 파싱)
) {}
