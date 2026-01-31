package com.usto.api.g2b.infrastructure.entity;

import com.usto.api.common.BaseTimeEntity;
import com.usto.api.common.utils.YesNoConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_G2B_SYNC_HIS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class G2bSyncHistoryJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SYNC_HIS_ID")
    private Long syncHisId;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "STS_YN", nullable = false, columnDefinition = "char(1)")
    private Boolean stsYn;

    // ===== SyncResult 컬럼(순서 유지) =====
    @Column(name = "BEGIN_DT", nullable = false, columnDefinition = "char(8)")
    private String begin;

    @Column(name = "END_DT", nullable = false, columnDefinition = "char(8)")
    private String end;

    @Column(name = "FETCHED", nullable = false)
    private Integer fetched;

    @Column(name = "DEDUPED", nullable = false)
    private Integer deduped;

    @Column(name = "DUPLICATED", nullable = false)
    private Integer duplicated;

    @Column(name = "INSERTED_CATEGORY", nullable = false)
    private Integer insertedCategory;

    @Column(name = "INSERTED_ITEM", nullable = false)
    private Integer insertedItem;

    @Column(name = "UPDATED_CATEGORY", nullable = false)
    private Integer updatedCategory;

    @Column(name = "UPDATED_ITEM", nullable = false)
    private Integer updatedItem;

    @Column(name = "CHANGED", nullable = false)
    private Long changed;

    // ===== 상태/감사 =====
    @Column(name = "ERR_CD", nullable = false, length = 3 , columnDefinition = "char(3)")
    private String errCd;

    @Column(name = "CRE_BY", nullable = false, length = 100)
    private String creBy;

    @Column(name = "CRE_AT", nullable = false)
    private LocalDateTime creAt;

    @PrePersist
    void onCreate() {
        if (creAt == null) creAt = LocalDateTime.now();
        if (creBy == null) creBy = "SYSTEM";
        if (errCd == null) errCd = "200";
        if (stsYn == null) stsYn = Boolean.FALSE;
    }
}
