package com.jandi.band_backend.promo.dto;

import com.jandi.band_backend.promo.entity.PromoComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PromoCommentRespDTO {
    
    private Integer id;
    private Integer promoId;
    private String description;
    private Integer creatorId;
    private String creatorName;
    private String creatorProfilePhoto;
    private Integer likeCount;
    private Boolean isLikedByUser; // 현재 사용자의 좋아요 상태
    private LocalDateTime createdAt;
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