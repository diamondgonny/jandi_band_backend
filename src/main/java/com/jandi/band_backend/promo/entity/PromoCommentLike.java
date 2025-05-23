package com.jandi.band_backend.promo.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "홍보 댓글 좋아요 엔티티")
@Entity
@Table(name = "promo_comment_like", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"promo_comment_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class PromoCommentLike {
    
    @Schema(description = "댓글 좋아요 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promo_comment_like_id")
    private Integer id;
    
    @Schema(description = "대상 댓글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_comment_id", nullable = false)
    private PromoComment promoComment;
    
    @Schema(description = "누른 사용자 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    
    @Schema(description = "좋아요 시각", example = "2024-01-01T00:00:00")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 
