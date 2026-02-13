package com.usto.api.ai.chat.infrastructure.entity;

import com.usto.api.ai.chat.domain.model.SenderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "TB_CHAT001D")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ChatMessageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_D_ID")
    private Long messageId;

    @Column(name = "CHAT_M_ID", columnDefinition = "BINARY(16)")
    private UUID threadId;

    @Column(name = "CONTENT", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "SENDER", length = 30, nullable = false)
    private SenderType sender; // USER, AI_BOT

    @Column(name = "ORG_CD", length = 7, nullable = false)
    private String orgCode;
}
