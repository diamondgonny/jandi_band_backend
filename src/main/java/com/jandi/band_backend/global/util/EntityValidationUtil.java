package com.jandi.band_backend.global.util;

import com.jandi.band_backend.club.entity.Club;
import com.jandi.band_backend.club.repository.ClubRepository;
import com.jandi.band_backend.global.exception.ClubNotFoundException;
import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.global.exception.TeamNotFoundException;
import com.jandi.band_backend.team.entity.Team;
import com.jandi.band_backend.team.entity.TeamEvent;
import com.jandi.band_backend.team.repository.TeamEventRepository;
import com.jandi.band_backend.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityValidationUtil {
    private final TeamRepository teamRepository;
    private final ClubRepository clubRepository;
    private final TeamEventRepository teamEventRepository;

    /**
     * 팀 존재 확인
     */
    public Team validateTeamExists(Integer teamId) {
        return teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new TeamNotFoundException("팀을 찾을 수 없습니다."));
    }

    /**
     * 동아리 존재 확인
     */
    public Club validateClubExists(Integer clubId) {
        return clubRepository.findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ClubNotFoundException("동아리를 찾을 수 없습니다."));
    }

    /**
     * 팀 이벤트 존재 확인
     */
    public TeamEvent validateTeamEventExists(Integer eventId) {
        return teamEventRepository.findByIdAndNotDeleted(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("연습 일정을 찾을 수 없습니다."));
    }

    /**
     * 특정 팀의 이벤트인지 확인
     */
    public TeamEvent validateTeamEventBelongsToTeam(Integer teamId, Integer eventId) {
        TeamEvent teamEvent = validateTeamEventExists(eventId);

        if (!teamEvent.getTeam().getId().equals(teamId)) {
            throw new ResourceNotFoundException("해당 팀의 연습 일정이 아닙니다.");
        }

        return teamEvent;
    }
}
