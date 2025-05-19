package com.jandi.band_backend.club.dto;

import com.jandi.band_backend.univ.dto.UniversityRespDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ClubRespDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Integer id;
        private String name;
        private UniversityRespDTO.SimpleResponse university;
        private Boolean isUnionClub;
        private String chatroomUrl;
        private String description;
        private String instagramId;
        private String photoUrl;
        private Integer memberCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleResponse {
        private Integer id;
        private String name;
        private String universityName;
        private Boolean isUnionClub;
        private String photoUrl;
        private Integer memberCount;
    }
}
