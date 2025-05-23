package com.jandi.band_backend.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDetailRespDTO {

    private Integer id;
    private String name;
    private ClubInfoDTO club;
    private CreatorInfoDTO creator;
    private List<MemberInfoDTO> members;
    private Integer memberCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClubInfoDTO {
        private Integer clubId;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreatorInfoDTO {
        private Integer userId;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberInfoDTO {
        private Integer userId;
        private String name;
        private String position;
    }
}
