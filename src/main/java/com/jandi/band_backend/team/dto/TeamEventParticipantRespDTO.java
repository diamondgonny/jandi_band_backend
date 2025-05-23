package com.jandi.band_backend.team.dto;

import com.jandi.band_backend.team.entity.TeamEventParticipant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamEventParticipantRespDTO {
    private Integer id;
    private Integer userId;
    private String userName;

    public static TeamEventParticipantRespDTO from(TeamEventParticipant participant) {
        TeamEventParticipantRespDTO response = new TeamEventParticipantRespDTO();
        response.setId(participant.getId());
        response.setUserId(participant.getUser().getId());
        response.setUserName(participant.getUser().getNickname());
        return response;
    }
} 