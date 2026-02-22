package com.usto.api.ai.chat.infrastructure.adapter;

import com.usto.api.ai.chat.domain.model.ChatThread;
import com.usto.api.ai.chat.domain.repository.ChatThreadRepository;
import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;
import com.usto.api.ai.chat.infrastructure.mapper.ChatThreadMapper;
import com.usto.api.ai.chat.infrastructure.repository.ChatThreadJpaRepository;
import com.usto.api.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatThreadRepositoryAdapter implements ChatThreadRepository {

    private final ChatThreadJpaRepository jpaRepository;

    @Override
    public void save(ChatThread master){
        ChatThreadJpaEntity masterEntity = ChatThreadMapper.toEntity(master);
        jpaRepository.save(masterEntity);
    }

    @Override
    public List<ChatThread> findByUsrId(String userName) {
        List<ChatThreadJpaEntity> entities = jpaRepository.findAllByUserId(userName);
        if(entities.isEmpty()){
            return null;
        }
        return entities.stream()
                .map(ChatThreadMapper::toDomain)
                .toList();
    }

    @Override
    public ChatThread findById(UUID threadId) {
        ChatThreadJpaEntity entity = jpaRepository.findByThreadId(threadId);
        if(entity == null){
            return null;
        }
        return ChatThreadMapper.toDomain(entity);
    }

    @Override
    public void deleteThread(UUID threadId) {
        jpaRepository.deleteById(threadId);
    }
}
