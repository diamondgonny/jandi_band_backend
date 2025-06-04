package com.jandi.band_backend.club.dto;

import com.jandi.band_backend.club.entity.ClubGalPhoto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubGalPhotoRespDTO {
    private Integer id;
    private String imageUrl;
    private Boolean isPinned;

    public ClubGalPhotoRespDTO(ClubGalPhoto photo) {
        id = photo.getId();
        imageUrl = photo.getImageUrl();
        isPinned = photo.getIsPinned();
    }
}
