package com.jandi.band_backend.club.dto;

import com.jandi.band_backend.club.entity.ClubGalPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ClubGalPhotoRespDetailDTO {
    private Integer id;
    private String uploader;
    private String imageUrl;
    private String description;
    private Boolean isPinned;
    private LocalDateTime uploadedAt;

    public ClubGalPhotoRespDetailDTO(ClubGalPhoto photo) {
        id = photo.getId();
        uploader = photo.getUploader().getNickname();
        imageUrl = photo.getImageUrl();
        description = photo.getDescription();
        isPinned = photo.getIsPinned();
        uploadedAt = photo.getUploadedAt();
    }
}
