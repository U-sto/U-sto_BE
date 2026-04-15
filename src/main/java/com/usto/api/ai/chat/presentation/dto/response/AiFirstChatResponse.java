package com.usto.api.ai.chat.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record AiFirstChatResponse(
        UUID threadId,
        AiChatResponse aiChatResponse

) {
        public record AiChatResponse(
                @JsonProperty("reply")
                String reply,      // AI 답변 내용

                @JsonProperty("action_buttons")
                List<ActionButton> actionButtons, // AI가 제안하는 다음 액션 버튼 리스트

                @JsonProperty("references")
                List<String> references,  // AI가 참고한 문서 리스트 (["doc1.pdf", "doc2.json"])

                @JsonProperty("created_at")
                String createdAt          // 생성 시간 (문자열로 받고 필요시 파싱)
        ) {
                public record ActionButton(
                        @JsonProperty("label")
                        String label,
                        @JsonProperty("url")
                        String url
                ) {
                }
        }
}

