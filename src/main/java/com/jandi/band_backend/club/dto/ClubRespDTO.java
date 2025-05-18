package com.jandi.band_backend.club.dto;

import com.jandi.band_backend.univ.dto.UniversityRespDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ClubRespDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Integer id;
        private String name;
        private List<UniversityRespDTO.SimpleResponse> universities;
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
        private List<String> universityNames;
        private String photoUrl;
        private Integer memberCount;
    }
}
