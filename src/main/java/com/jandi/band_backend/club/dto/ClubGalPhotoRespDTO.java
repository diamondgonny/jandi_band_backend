package com.jandi.band_backend.club.dto;

import com.jandi.band_backend.club.entity.ClubGalPhoto;
import lombok.Data;

@Data
public class ClubGalPhotoRespDTO {
    private Integer photoId;
    private Integer uploaderId;
    private String uploaderName;
    private String imageUrl;
    private Boolean isPinned;
    private Boolean isPublic;

    public ClubGalPhotoRespDTO(ClubGalPhoto photo) {
        photoId = photo.getId();
        uploaderId = photo.getUploader().getId();
        uploaderName = photo.getUploader().getNickname();
        imageUrl = photo.getImageUrl();
        isPinned = photo.getIsPinned();
        isPublic = photo.getIsPublic();
    }
}
