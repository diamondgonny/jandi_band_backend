package com.jandi.band_backend.club.dto;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.entity.ClubPhoto;
import com.jandi.band_backend.global.util.TimeUtil;
import com.jandi.band_backend.univ.dto.UniversityRespDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubRespDTO {
    private Integer id;
    private String name;
    private UniversityRespDTO university;
    private Boolean isUnionClub;
    private String chatroomUrl;
    private String description;
    private String instagramId;
    private String photoUrl;
    private Integer memberCount;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
