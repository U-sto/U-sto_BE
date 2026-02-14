package com.usto.api.ai.chat.infrastructure.entity;

import com.usto.api.ai.chat.domain.model.SenderType;
import com.usto.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_CHAT001D")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE TB_CHAT001D SET del_yn = 'Y', del_at = NOW() WHERE chat_m_id = ?")
@Where(clause = "DEL_YN = 'N'")
public class ChatMessageJpaEntity extends BaseTimeEntity {

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

    @Column(name = "ORG_CD", length = 7, nullable = false,columnDefinition = "CHAR(7)")
    private String orgCode;

    @Builder.Default
    @Column(name = "DEL_YN", nullable = false, length = 1, columnDefinition = "char(1)")
    private String delYn = "N";   // 삭제여부

    @Column(name = "DEL_AT")
    private LocalDateTime delAt;  // 삭제일시
}
