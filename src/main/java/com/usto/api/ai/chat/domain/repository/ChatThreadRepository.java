package com.usto.api.ai.chat.domain.repository;

import com.usto.api.ai.chat.domain.model.ChatThread;

import java.util.Optional;
import java.util.UUID;

public interface ChatThreadRepository {
    ChatThread findById(UUID threadId);
}
