package com.usto.api.item.common.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprStatus {
    WAIT("작성중"),     // 처음 등록했을 때 (수정/삭제/승인요청 가능)
    REQUEST("승인요청중"),   // 승인요청 버튼을 눌렀을 때 (수정/삭제 불가, 승인취소 가능)
    APPROVED("확정"),       // MANAGER가 승인했을 때 (수정/삭제/취소 불가)
    REJECTED("반려");        // MANAGER가 반려했을 때

    @JsonValue
    private final String description;
}