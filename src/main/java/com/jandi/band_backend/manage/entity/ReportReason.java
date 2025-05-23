package com.jandi.band_backend.manage.entity;

import com.jandi.band_backend.promo.entity.PromoCommentReport;
import com.jandi.band_backend.promo.entity.PromoReport;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "신고 사유 엔티티")
@Entity
@Table(name = "report_reason")
@Getter
@Setter
@NoArgsConstructor
public class ReportReason {
    
    @Schema(description = "신고 사유 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_reason_id")
    private Integer id;
    
    @Schema(description = "사유 코드", example = "SPAM")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    @Schema(description = "상세 설명", example = "스팸성 게시물")
    @Column(name = "description", nullable = false, length = 255)
    private String description;
    
    @Schema(description = "생성 시각", example = "2024-01-01T00:00:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시각", example = "2024-01-01T00:00:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Schema(description = "홍보 게시글 신고 목록")
    @OneToMany(mappedBy = "reportReason")
    private List<PromoReport> promoReports = new ArrayList<>();
    
    @Schema(description = "홍보 댓글 신고 목록")
    @OneToMany(mappedBy = "reportReason")
    private List<PromoCommentReport> promoCommentReports = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 
