package com.jandi.band_backend.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubMembersRespDTO {
    private Integer id;
    private List<MemberInfoDTO> members;
    private Integer vocalCount;
    private Integer guitarCount;
    private Integer keyboardCount;
    private Integer bassCount;
    private Integer drumCount;
    private Integer totalMemberCount;

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
