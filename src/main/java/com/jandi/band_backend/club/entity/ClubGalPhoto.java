package com.jandi.band_backend.club.entity;

import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "동아리 갤러리 사진 엔티티")
@Entity
@Table(name = "club_gal_photo")
@Getter
@Setter
@NoArgsConstructor
public class ClubGalPhoto {

    @Schema(description = "갤러리 사진 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_gal_photo_id")
    private Integer id;

    @Schema(description = "소속 동아리")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Schema(description = "업로더 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_user_id", nullable = false)
    private Users uploader;

    @Schema(description = "이미지 URL", example = "https://s3.amazonaws.com/bucket/club1/gallery/photo1.jpg")
    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;

    @Schema(description = "사진 설명", example = "2024년 정기공연 무대 사진")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Schema(description = "상단 고정 여부", example = "false")
    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @Schema(description = "공개 여부", example = "true")
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

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
