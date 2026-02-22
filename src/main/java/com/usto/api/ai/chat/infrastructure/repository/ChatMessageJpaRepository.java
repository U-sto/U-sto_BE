package com.usto.api.ai.chat.infrastructure.repository;
import com.usto.api.ai.chat.infrastructure.entity.ChatMessageJpaEntity;
import com.usto.api.ai.chat.infrastructure.entity.ChatThreadJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;


public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageJpaEntity, Long> {
    ChatMessageJpaEntity save(ChatMessageJpaEntity entity);

    @Query(value = """
    SELECT content
    FROM TB_CHAT001D D JOIN TB_CHAT001M M
    ON D.CHAT_M_ID = M.CHAT_M_ID
    WHERE D.CONTENT LIKE CONCAT('%', :content, '%')
      AND M.USR_ID = :username
    """, nativeQuery = true)
    List<String> findByContent(String content, String username);

    List<ChatMessageJpaEntity> findByThreadId(UUID threadId);
}
