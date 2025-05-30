package com.jandi.band_backend.team.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jandi.band_backend.global.exception.BadRequestException;
import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.team.dto.ScheduleSuggestionRespDTO;
import com.jandi.band_backend.team.dto.TimetableReqDTO;
import com.jandi.band_backend.team.dto.TimetableUpdateReqDTO;
import com.jandi.band_backend.team.dto.TimetableRespDTO;
import com.jandi.band_backend.team.entity.Team;
import com.jandi.band_backend.team.entity.TeamMember;
import com.jandi.band_backend.team.repository.TeamMemberRepository;
import com.jandi.band_backend.team.repository.TeamRepository;
import com.jandi.band_backend.user.service.UserTimetableService;
import com.jandi.band_backend.user.dto.UserTimetableDetailsRespDTO;
import com.jandi.band_backend.team.util.TeamTimetableUtil;
import com.jandi.band_backend.global.util.PermissionValidationUtil;
import com.jandi.band_backend.global.util.UserValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamTimetableService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserTimetableService userTimetableService;
    private final ObjectMapper objectMapper;
    private final TeamTimetableUtil teamTimetableUtil;
    private final PermissionValidationUtil permissionValidationUtil;
    private final UserValidationUtil userValidationUtil;

    /**
     * 팀내 스케줄 조율 제안 ('시간 언제 돼? 모드' 시작)
     */
    @Transactional
    public ScheduleSuggestionRespDTO startScheduleSuggestion(Integer teamId, Integer currentUserId) {
        Team team = teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다."));

        TeamMember teamMember = permissionValidationUtil.validateTeamMemberAccess(teamId, currentUserId, "팀원만 접근할 수 있습니다.");

        // suggested_schedule_at을 현재 시간으로 설정
        LocalDateTime now = LocalDateTime.now();
        team.setSuggestedScheduleAt(now);
        teamRepository.save(team);

        // TODO: 팀원들에게 카카오톡 알림 발송 로직 추가
        // (팀원 수가 1이면 알림 안 보내게 할 것?)

        return ScheduleSuggestionRespDTO.builder()
                .teamId(teamId)
                .suggestedScheduleAt(now)
                .suggesterUserId(currentUserId)
                .suggesterName(teamMember.getUser().getNickname())
                .build();
    }

    /**
     * 팀내 내 시간표 등록
     */
    @Transactional
    public TimetableRespDTO registerMyTimetable(Integer teamId, TimetableReqDTO reqDTO, Integer currentUserId) {
        TeamMember teamMember = validateTeamAndGetTeamMember(teamId, currentUserId);

        UserTimetableDetailsRespDTO userTimetable = userTimetableService.getMyTimetableById(currentUserId, reqDTO.getUserTimetableId());

        return saveTeamMemberTimetableAndBuildResponse(teamMember, userTimetable.getTimetableData(), currentUserId, teamId);
    }

    /**
     * 팀내 내 시간표 수정
     */
    @Transactional
    public TimetableRespDTO updateMyTimetable(Integer teamId, TimetableUpdateReqDTO reqDTO, Integer currentUserId) {
        TeamMember teamMember = validateTeamAndGetTeamMember(teamId, currentUserId);

        teamTimetableUtil.validateTimetableRequest(reqDTO);

        return saveTeamMemberTimetableAndBuildResponse(teamMember, reqDTO.getTimetableData(), currentUserId, teamId);
    }

    /**
     * 팀 존재 확인 및 팀멤버 권한 검증
     */
    private TeamMember validateTeamAndGetTeamMember(Integer teamId, Integer currentUserId) {
        teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다."));

        return permissionValidationUtil.validateTeamMemberAccess(teamId, currentUserId, "본인의 시간표만 입력할 수 있습니다.");
    }

    /**
     * 팀멤버 시간표 데이터 저장 및 응답 DTO 생성
     */
    private TimetableRespDTO saveTeamMemberTimetableAndBuildResponse(TeamMember teamMember, JsonNode timetableData, Integer currentUserId, Integer teamId) {
        try {
            // 시간표 데이터를 JSON으로 변환하여 저장 (JsonNode를 String으로 변환)
            String timetableJson = objectMapper.writeValueAsString(timetableData);
            teamMember.setTimetableData(timetableJson);
            teamMember.setUpdatedTimetableAt(LocalDateTime.now());

            teamMemberRepository.save(teamMember);

            // TODO: 모든 팀원이 시간표를 제출했는지 확인하고, 완료되면 카카오톡 알림 발송
            // (팀원 수가 1이면 알림 안 보내게 할 것?)

            return TimetableRespDTO.builder()
                    .userId(currentUserId)
                    .teamId(teamId)
                    .timetableData(timetableData)
                    .updatedTimetableAt(teamMember.getUpdatedTimetableAt())
                    .build();

        } catch (JsonProcessingException e) {
            log.error("시간표 데이터 JSON 변환 오류: {}", e.getMessage());
            throw new BadRequestException("시간표 데이터 처리 중 오류가 발생했습니다.");
        }
    }
}
