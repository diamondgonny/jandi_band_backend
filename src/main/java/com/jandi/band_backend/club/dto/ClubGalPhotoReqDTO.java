package com.jandi.band_backend.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class ClubGalPhotoReqDTO {
    MultipartFile image;
    String description;
    Boolean isPublic;
}
