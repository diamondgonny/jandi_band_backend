package com.jandi.band_backend.club.dto;

import com.jandi.band_backend.club.entity.ClubGalPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ClubGalPhotoRespDetailDTO {
    private Integer photoId;
    private Integer uploaderId;
    private String uploaderName;
    private String imageUrl;
    private String description;
    private Boolean isPinned;
    private Boolean isPublic;
    private LocalDateTime uploadedAt;

    public ClubGalPhotoRespDetailDTO(ClubGalPhoto photo) {
        photoId = photo.getId();
        uploaderId = photo.getUploader().getId();
        uploaderName = photo.getUploader().getNickname();
        imageUrl = photo.getImageUrl();
        description = photo.getDescription();
        isPinned = photo.getIsPinned();
        isPublic = photo.getIsPublic();
        uploadedAt = photo.getUploadedAt();
    }
}
