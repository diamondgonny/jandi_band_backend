package com.jandi.band_backend.promo.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "홍보 사진 엔티티")
@Entity
@Table(name = "promo_photo", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"promo_id", "is_current"}))
@Getter
@Setter
@NoArgsConstructor
public class PromoPhoto {

    @Schema(description = "홍보 사진 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promo_photo_id")
    private Integer id;

    @Schema(description = "대상 홍보 글")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_id", nullable = false)
    private Promo promo;

    @Schema(description = "업로더 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_user_id", nullable = false)
    private Users uploader;

    @Schema(description = "이미지 URL", example = "https://s3.amazonaws.com/bucket/promo1/poster.jpg")
    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;
    
    @Schema(description = "현재 사용 중인지 여부", example = "true")
    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = true;

    @Schema(description = "업로드 시각", example = "2024-01-01T00:00:00")
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @Schema(description = "삭제 시각", example = "2024-01-01T00:00:00")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
} 
