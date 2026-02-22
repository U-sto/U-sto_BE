package com.usto.api.ai.chat.domain.service;

import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.common.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class ChatThreadPolicy {
    public void validateOwnership(ChatThread thread, String username) {
        if(!thread.getUserId().equals(username)){
            throw new BusinessException("당신의 채팅방이 아닙니다.");
        }
    }
}
