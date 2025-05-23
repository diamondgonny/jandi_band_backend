package com.jandi.band_backend.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "사용자 프로필 사진 엔티티")
@Entity
@Table(name = "user_photo", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "is_current"}))
@Getter
@Setter
@NoArgsConstructor
public class UserPhoto {

    @Schema(description = "사용자 프로필 사진 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_photo_id")
    private Integer id;

    @Schema(description = "해당 유저")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Schema(description = "저장된 이미지 URL", example = "https://s3.amazonaws.com/bucket/user1/profile.jpg")
    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;
    
    @Schema(description = "현재 프로필 사진 여부", example = "true")
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
