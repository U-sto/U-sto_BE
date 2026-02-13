package com.usto.api.ai.chat.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TB_CHAT001M")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ChatThreadJpaEntity extends BaseTimeEntity {

    @Id
    @Column(name = "CHAT_M_ID", columnDefinition = "BINARY(16)")
    private UUID threadId;

    @Column(name = "USR_ID", length = 50, nullable = false)
    private String userId;

    @Column(name = "TITLE", columnDefinition = "TEXT")
    private String title;

    @Column(name = "LAST_MSG_AT", nullable = false)
    private LocalDateTime lastMessageAt;

    @Column(name = "ORG_CD", length = 7, nullable = false)
    private String orgCode;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMessageJpaEntity> messages = new ArrayList<>();

}
