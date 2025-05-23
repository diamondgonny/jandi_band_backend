package com.jandi.band_backend.promo.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "홍보 댓글 엔티티")
@Entity
@Table(name = "promo_comment")
@Getter
@Setter
@NoArgsConstructor
public class PromoComment {
    
    @Schema(description = "홍보 댓글 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promo_comment_id")
    private Integer id;
    
    @Schema(description = "대상 홍보 글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_id", nullable = false)
    private Promo promo;
    
    @Schema(description = "댓글 작성자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id", nullable = false)
    private Users creator;
    
    @Schema(description = "댓글 내용", example = "정말 기대되는 공연이네요!")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Schema(description = "작성 시각", example = "2024-01-01T00:00:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시각", example = "2024-01-01T00:00:00")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Schema(description = "삭제 시각", example = "2024-01-01T00:00:00")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Schema(description = "댓글 좋아요 목록")
    @OneToMany(mappedBy = "promoComment")
    private List<PromoCommentLike> likes = new ArrayList<>();
    
    @Schema(description = "댓글 신고 목록")
    @OneToMany(mappedBy = "promoComment")
    private List<PromoCommentReport> reports = new ArrayList<>();
    
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
