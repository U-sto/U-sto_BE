package com.usto.api.ai.chat.domain.service;

import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatThreadPolicy {
    public void validateOwnership(ChatThread thread, String username) {
        if(!thread.getUserId().equals(username)){
            log.info("당신의 id = {}, 채팅방 id =     {}",thread.getUserId(),username);
            throw new BusinessException("당신의 채팅방이 아닙니다.");
        }
    }
}
