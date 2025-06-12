package com.jandi.band_backend.club.dto;

import com.jandi.band_backend.club.entity.ClubGalPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ClubGalPhotoRespDetailDTO {
    private Integer photo_id;
    private Integer uploader_id;
    private String uploader_name;
    private String imageUrl;
    private String description;
    private Boolean isPinned;
    private Boolean isPublic;
    private LocalDateTime uploadedAt;

    public ClubGalPhotoRespDetailDTO(ClubGalPhoto photo) {
        photo_id = photo.getId();
        uploader_id = photo.getUploader().getId();
        uploader_name = photo.getUploader().getNickname();
        imageUrl = photo.getImageUrl();
        description = photo.getDescription();
        isPinned = photo.getIsPinned();
        isPublic = photo.getIsPublic();
        uploadedAt = photo.getUploadedAt();
    }
}
