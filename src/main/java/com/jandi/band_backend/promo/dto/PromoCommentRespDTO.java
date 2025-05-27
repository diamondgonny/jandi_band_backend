package com.jandi.band_backend.promo.dto;

import com.jandi.band_backend.promo.entity.PromoComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "공연 홍보 댓글 응답 DTO")
public class PromoCommentRespDTO {
    
    @Schema(description = "댓글 ID", example = "1")
    private Integer id;
    
    @Schema(description = "공연 홍보 ID", example = "1")
    private Integer promoId;
    
    @Schema(description = "댓글 내용", example = "정말 기대되는 공연이네요!")
    private String description;
    
    @Schema(description = "작성자 ID", example = "1")
    private Integer creatorId;
    
    @Schema(description = "작성자명", example = "홍길동")
    private String creatorName;
    
    @Schema(description = "작성자 프로필 사진 URL", example = "https://example.com/profile.jpg")
    private String creatorProfilePhoto;
    
    @Schema(description = "댓글 좋아요 수", example = "5")
    private Integer likeCount;
    
    @Schema(description = "현재 사용자의 댓글 좋아요 상태 (true: 좋아요 누름, false: 좋아요 안 누름, null: 인증되지 않은 사용자)", example = "true")
    private Boolean isLikedByUser;
    
    @Schema(description = "댓글 생성일시", example = "2024-03-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "댓글 수정일시", example = "2024-03-15T10:30:00")
    private LocalDateTime updatedAt;
    
    public static PromoCommentRespDTO from(PromoComment comment) {
        // 사용자 프로필 사진 URL 조회
        String creatorProfilePhoto = comment.getCreator().getPhotos().stream()
                .filter(photo -> photo.getIsCurrent() && photo.getDeletedAt() == null)
                .map(photo -> photo.getImageUrl())
                .findFirst()
                .orElse(null);
        
        return PromoCommentRespDTO.builder()
                .id(comment.getId())
                .promoId(comment.getPromo().getId())
                .description(comment.getDescription())
                .creatorId(comment.getCreator().getId())
                .creatorName(comment.getCreator().getNickname())
                .creatorProfilePhoto(creatorProfilePhoto)
                .likeCount(comment.getLikes().size())
                .isLikedByUser(null) // 기본값은 null (인증되지 않은 사용자)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public static PromoCommentRespDTO from(PromoComment comment, Boolean isLikedByUser) {
        PromoCommentRespDTO response = from(comment);
        return response.toBuilder()
                .isLikedByUser(isLikedByUser)
                .build();
    }
} 