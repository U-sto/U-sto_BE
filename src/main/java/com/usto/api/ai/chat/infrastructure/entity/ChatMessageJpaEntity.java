package com.usto.api.ai.chat.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    private Long chatDId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_M_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatThreadJpaEntity threadMId;

    @Column(name = "CONTENT", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "SENDER", length = 30, nullable = false)
    private SenderType sender; // USER, AI_BOT

    @Column(name = "ORG_CD", length = 7, nullable = false)
    private String orgCode;

    public enum SenderType {
        USER, AI_BOT
    }
}
