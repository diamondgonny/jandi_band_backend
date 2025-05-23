package com.jandi.band_backend.promo.entity;

import com.jandi.band_backend.manage.entity.ReportReason;
import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "홍보 글 신고 엔티티")
@Entity
@Table(name = "promo_report", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"promo_id", "reporter_user_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class PromoReport {
    
    @Schema(description = "홍보 글 신고 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promo_report_id")
    private Integer id;
    
    @Schema(description = "신고 대상 홍보 글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_id", nullable = false)
    private Promo promo;
    
    @Schema(description = "신고자 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_user_id", nullable = false)
    private Users reporter;
    
    @Schema(description = "신고 사유 코드")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_reason_id", nullable = false)
    private ReportReason reportReason;
    
    @Schema(description = "추가 설명", example = "부적절한 내용이 포함되어 있습니다.")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Schema(description = "신고 시각", example = "2024-01-01T00:00:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 
