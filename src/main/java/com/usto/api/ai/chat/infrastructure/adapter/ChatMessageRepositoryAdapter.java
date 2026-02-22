package com.usto.api.ai.chat.infrastructure.adapter;

import com.usto.api.ai.chat.domain.repository.ChatMessageRepository;
import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;
import com.usto.api.ai.chat.infrastructure.repository.ChatMessageJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


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
}
