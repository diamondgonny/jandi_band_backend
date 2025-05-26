package com.jandi.band_backend.team.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class TeamTimetablesRespDTO {

    private TeamInfoDTO teamInfo;
    private List<MemberTimetableDTO> members;
    private SubmissionProgressDTO submissionProgress;

    @Getter
    @Builder
    public static class TeamInfoDTO {
        private Integer id;
        private String name;
        private LocalDateTime suggestedScheduleAt;
        private Boolean isScheduleActive;
    }

    @Getter
    @Builder
    public static class MemberTimetableDTO {
        private Integer userId;
        private String username;
        private String position;
        private LocalDateTime timetableUpdatedAt;
        private Boolean isSubmitted;
        private Map<String, List<String>> timetableData;
    }

    @Getter
    @Builder
    public static class SubmissionProgressDTO {
        private Integer submitted;
        private Integer total;
    }
}
