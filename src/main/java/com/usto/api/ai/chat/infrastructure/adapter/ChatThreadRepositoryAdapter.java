package com.usto.api.ai.chat.infrastructure.adapter;

import com.usto.api.ai.chat.domain.repository.ChatThreadRepository;
import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;
import com.usto.api.ai.chat.infrastructure.repository.ChatThreadJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatThreadRepositoryAdapter implements ChatThreadRepository {

    private final ChatThreadJpaRepository jpaRepository;

    @Override
    public void save(ChatThreadJpaEntity maseterEntity){
        jpaRepository.save(maseterEntity);
    }
}
