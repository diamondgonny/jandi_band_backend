package com.jandi.band_backend.team.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleSuggestionRespDTO {

    private Integer teamId;
    private LocalDateTime suggestedScheduleAt;
    private Integer suggesterUserId;
    private String suggesterName;
}
