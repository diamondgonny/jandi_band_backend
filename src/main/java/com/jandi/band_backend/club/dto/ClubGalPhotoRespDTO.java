package com.jandi.band_backend.club.dto;

import com.jandi.band_backend.club.entity.ClubGalPhoto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubGalPhotoRespDTO {
    private Integer id;
    private String uploader;
    private String imageUrl;
    private String description;
    private Boolean isPinned;
    private LocalDateTime uploadedAt;

    public ClubGalPhotoRespDTO(ClubGalPhoto photo) {
        id = photo.getId();
        uploader = photo.getUploader().getNickname();
        imageUrl = photo.getImageUrl();
        description = photo.getDescription();
        isPinned = photo.getIsPinned();
        uploadedAt = photo.getUploadedAt();
    }
}
