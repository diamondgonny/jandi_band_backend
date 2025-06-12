package com.jandi.band_backend.club.dto;

import com.jandi.band_backend.club.entity.ClubGalPhoto;
import lombok.Data;

@Data
public class ClubGalPhotoRespDTO {
    private Integer photo_id;
    private Integer uploader_id;
    private String uploader_name;
    private String imageUrl;
    private Boolean isPinned;
    private Boolean isPublic;

    public ClubGalPhotoRespDTO(ClubGalPhoto photo) {
        photo_id = photo.getId();
        uploader_id = photo.getUploader().getId();
        uploader_name = photo.getUploader().getNickname();
        imageUrl = photo.getImageUrl();
        isPinned = photo.getIsPinned();
        isPublic = photo.getIsPublic();
    }
}
