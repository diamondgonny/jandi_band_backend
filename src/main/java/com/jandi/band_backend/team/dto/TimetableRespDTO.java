package com.jandi.band_backend.team.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class TimetableRespDTO {

    private Integer userId;
    private Integer teamId;
    private Map<String, List<String>> timetableData;
    private LocalDateTime updatedTimetableAt;
}
