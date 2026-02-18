package com.usto.api.ai.chat.domain.repository;

import com.usto.api.ai.chat.domain.model.ChatThread;

public interface ChatThreadRepository {

    void save(ChatThread chatThread);
}
