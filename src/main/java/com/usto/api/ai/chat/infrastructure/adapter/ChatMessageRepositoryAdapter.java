package com.usto.api.ai.chat.infrastructure.adapter;

import com.usto.api.ai.chat.domain.model.ChatMessage;
import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.ai.chat.domain.repository.ChatMessageRepository;
import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;
import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;
import com.usto.api.ai.chat.infrastructure.mapper.ChatMessageMapper;
import com.usto.api.ai.chat.infrastructure.mapper.ChatThreadMapper;
import com.usto.api.ai.chat.infrastructure.repository.ChatMessageJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageRepositoryAdapter implements ChatMessageRepository {

    private final ChatMessageJpaRepository jpaRepository;


    @Override
    public void save(ChatMessageJpaEntity entity) {
        jpaRepository.save(entity);
    }

    @Override
    public List<String> findByContent(String comment, String username) {
        return jpaRepository.findByContent(comment , username);
    }

    @Override
    public List<ChatMessage> findByThreadIdOrderByCreAtAsc(UUID threadId) {
        List<ChatMessageJpaEntity> entities = jpaRepository.findByThreadIdOrderByCreAtAsc(threadId);

        List<ChatMessage> result = new ArrayList<>();
        for (ChatMessageJpaEntity entity : entities) {
            result.add(ChatMessageMapper.toDomain(entity));
        }

        return result;
    }
}
