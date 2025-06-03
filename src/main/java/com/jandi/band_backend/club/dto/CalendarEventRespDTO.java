package com.jandi.band_backend.club.dto;

import com.jandi.band_backend.club.entity.ClubEvent;
import com.jandi.band_backend.team.entity.TeamEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventRespDTO {
    private Integer id;
    private String name;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private EventType eventType;    // 일정 유형 구분
    private Integer teamId;         // 팀 일정인 경우 팀 ID
    private String teamName;        // 팀 일정인 경우 팀 이름
    private String noPosition;      // 팀 일정인 경우 제외 포지션

    public enum EventType {
        CLUB_EVENT,     // 동아리 일정
        TEAM_EVENT      // 팀 일정
    }

    // ClubEvent에서 변환하는 정적 팩토리 메서드
    public static CalendarEventRespDTO fromClubEvent(ClubEvent clubEvent) {
        return CalendarEventRespDTO.builder()
                .id(clubEvent.getId())
                .name(clubEvent.getName())
                .startDatetime(clubEvent.getStartDatetime())
                .endDatetime(clubEvent.getEndDatetime())
                .eventType(EventType.CLUB_EVENT)
                .teamId(null)
                .teamName(null)
                .noPosition(null)
                .build();
    }

    // TeamEvent에서 변환하는 정적 팩토리 메서드
    public static CalendarEventRespDTO fromTeamEvent(TeamEvent teamEvent) {
        return CalendarEventRespDTO.builder()
                .id(teamEvent.getId())
                .name(teamEvent.getName())
                .startDatetime(teamEvent.getStartDatetime())
                .endDatetime(teamEvent.getEndDatetime())
                .eventType(EventType.TEAM_EVENT)
                .teamId(teamEvent.getTeam().getId())
                .teamName(teamEvent.getTeam().getName())
                .noPosition(teamEvent.getNoPosition() != null ? teamEvent.getNoPosition().name() : null)
                .build();
    }
} 