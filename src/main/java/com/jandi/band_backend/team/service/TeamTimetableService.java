package com.jandi.band_backend.team.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jandi.band_backend.club.repository.ClubMemberRepository;
import com.jandi.band_backend.global.exception.BadRequestException;
import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.global.exception.UnauthorizedClubAccessException;
import com.jandi.band_backend.team.dto.*;
import com.jandi.band_backend.team.entity.Team;
import com.jandi.band_backend.team.entity.TeamMember;
import com.jandi.band_backend.team.repository.TeamMemberRepository;
import com.jandi.band_backend.team.repository.TeamRepository;
import com.jandi.band_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamTimetableService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // 유효한 요일 목록
    private static final Set<String> VALID_DAYS = Set.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

    // 유효한 시간 형식 패턴 (HH:mm, 30분 단위: 00:00~23:30)
    private static final Pattern TIME_PATTERN = Pattern.compile("^(0?[0-9]|1[0-9]|2[0-3]):(00|30)$");

    /**
     * 팀내 스케줄 조율 제안 ('시간 언제 돼? 모드' 시작)
     */
    @Transactional
    public ScheduleSuggestionRespDTO startScheduleSuggestion(Integer teamId, Integer currentUserId) {
        // 팀 존재 확인
        Team team = teamRepository.findByIdAndNotDeleted(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다."));

        // 팀원 권한 확인 (팀원만 스케줄 조율 제안 가능)
        TeamMember teamMember = teamMemberRepository.findByTeamIdAndUserId(teamId, currentUserId)
                .orElseThrow(() -> new UnauthorizedClubAccessException("팀원만 접근할 수 있습니다."));

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
     * 팀내 팀원들 시간표 목록 조회
     */
    public TeamTimetablesRespDTO getTeamTimetables(Integer teamId, Integer currentUserId) {
        // 팀 존재 확인
        Team team = teamRepository.findByIdAndNotDeleted(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다."));

        // 동아리 부원 권한 확인 (동아리 부원이면 조회 가능)
        clubMemberRepository.findByClubIdAndUserId(team.getClub().getId(), currentUserId)
                .orElseThrow(() -> new UnauthorizedClubAccessException("해당 동아리 부원만 접근할 수 있습니다."));

        // suggestedScheduleAt이 null이면 예외
        if (team.getSuggestedScheduleAt() == null) {
            throw new RuntimeException("suggestedScheduleAt이 null입니다.");
        }

        // 팀 멤버 목록 조회
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamId(teamId);

        // 제출 현황 계산
        int submittedCount = 0;
        List<TeamTimetablesRespDTO.MemberTimetableDTO> memberTimetables = new ArrayList<>();

        for (TeamMember teamMember : teamMembers) {
            // suggestedScheduleAt 이후에 시간표를 제출했는지 확인
            boolean isSubmitted = teamMember.getUpdatedTimetableAt() != null &&
                    teamMember.getUpdatedTimetableAt().isAfter(team.getSuggestedScheduleAt());
            if (isSubmitted) {
                submittedCount++;
            }

            Map<String, List<String>> timetableData = null;
            if (teamMember.getTimetableData() != null) {
                try {
                    timetableData = objectMapper.readValue(teamMember.getTimetableData(),
                            new TypeReference<Map<String, List<String>>>() {});
                } catch (JsonProcessingException e) {
                    log.error("시간표 데이터 파싱 오류: {}", e.getMessage());
                }
            }

            TeamTimetablesRespDTO.MemberTimetableDTO memberDTO = TeamTimetablesRespDTO.MemberTimetableDTO.builder()
                    .userId(teamMember.getUser().getId())
                    .username(teamMember.getUser().getNickname())
                    .position(teamMember.getUser().getPosition() != null ?
                            teamMember.getUser().getPosition().name() : null)
                    .timetableUpdatedAt(teamMember.getUpdatedTimetableAt())
                    .isSubmitted(isSubmitted)
                    .timetableData(timetableData)
                    .build();

            memberTimetables.add(memberDTO);
        }

        // 팀 정보
        TeamTimetablesRespDTO.TeamInfoDTO teamInfo = TeamTimetablesRespDTO.TeamInfoDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .suggestedScheduleAt(team.getSuggestedScheduleAt())
                .isScheduleActive(team.getSuggestedScheduleAt() != null)
                .build();

        // 제출 진행률
        TeamTimetablesRespDTO.SubmissionProgressDTO submissionProgress = TeamTimetablesRespDTO.SubmissionProgressDTO.builder()
                .submitted(submittedCount)
                .total(teamMembers.size())
                .build();

        return TeamTimetablesRespDTO.builder()
                .teamInfo(teamInfo)
                .members(memberTimetables)
                .submissionProgress(submissionProgress)
                .build();
    }

    /**
     * 팀내 내 시간표 입력
     */
    @Transactional
    public TimetableRespDTO submitMyTimetable(Integer teamId, TimetableReqDTO reqDTO, Integer currentUserId) {
        // 시간표 데이터 유효성 검사
        validateTimetableData(reqDTO.getTimetableData());

        // 팀 존재 확인
        Team team = teamRepository.findByIdAndNotDeleted(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다."));

        // 본인만 시간표 입력 가능하도록 권한 확인
        TeamMember teamMember = teamMemberRepository.findByTeamIdAndUserId(teamId, currentUserId)
                .orElseThrow(() -> new InvalidAccessException("본인의 시간표만 입력할 수 있습니다."));

        try {
            // 시간표 데이터를 JSON으로 변환하여 저장
            String timetableJson = objectMapper.writeValueAsString(reqDTO.getTimetableData());
            teamMember.setTimetableData(timetableJson);
            teamMember.setUpdatedTimetableAt(LocalDateTime.now());

            teamMemberRepository.save(teamMember);

            // TODO: 모든 팀원이 시간표를 제출했는지 확인하고, 완료되면 카카오톡 알림 발송
            // (팀원 수가 1이면 알림 안 보내게 할 것?)

            return TimetableRespDTO.builder()
                    .userId(currentUserId)
                    .teamId(teamId)
                    .timetableData(reqDTO.getTimetableData())
                    .updatedTimetableAt(teamMember.getUpdatedTimetableAt())
                    .build();

        } catch (JsonProcessingException e) {
            log.error("시간표 데이터 JSON 변환 오류: {}", e.getMessage());
            throw new BadRequestException("시간표 데이터 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 팀내 내 시간표 조회
     */
    public TimetableRespDTO getMyTimetable(Integer teamId, Integer currentUserId) {
        // 팀 존재 확인
        Team team = teamRepository.findByIdAndNotDeleted(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다."));

        // 동아리 부원 권한 확인 (GET은 동아리 부원 권한 필요)
        clubMemberRepository.findByClubIdAndUserId(team.getClub().getId(), currentUserId)
                .orElseThrow(() -> new UnauthorizedClubAccessException("해당 동아리 부원만 접근할 수 있습니다."));

        // 팀원 확인 (본인 시간표만 조회 가능)
        TeamMember teamMember = teamMemberRepository.findByTeamIdAndUserId(teamId, currentUserId)
                .orElseThrow(() -> new UnauthorizedClubAccessException("팀원의 시간표만 조회할 수 있습니다."));

        Map<String, List<String>> timetableData = null;
        if (teamMember.getTimetableData() != null) {
            try {
                timetableData = objectMapper.readValue(teamMember.getTimetableData(),
                        new TypeReference<Map<String, List<String>>>() {});
            } catch (JsonProcessingException e) {
                log.error("시간표 데이터 파싱 오류: {}", e.getMessage());
                throw new BadRequestException("시간표 데이터를 읽는 중 오류가 발생했습니다.");
            }
        }

        return TimetableRespDTO.builder()
                .userId(currentUserId)
                .teamId(teamId)
                .timetableData(timetableData)
                .updatedTimetableAt(teamMember.getUpdatedTimetableAt())
                .build();
    }



    /**
     * 시간표 데이터 유효성 검사
     */
    private void validateTimetableData(Map<String, List<String>> timetableData) {
        if (timetableData == null) {
            throw new BadRequestException("시간표 데이터는 필수입니다.");
        }

        // 모든 요일이 포함되어 있는지 확인
        if (!timetableData.keySet().equals(VALID_DAYS)) {
            throw new BadRequestException("모든 요일(Mon, Tue, Wed, Thu, Fri, Sat, Sun)이 포함되어야 합니다.");
        }

        // 각 요일의 시간 데이터 검증
        for (Map.Entry<String, List<String>> entry : timetableData.entrySet()) {
            String day = entry.getKey();
            List<String> times = entry.getValue();

            if (times == null) {
                throw new BadRequestException(day + " 요일의 시간 데이터가 null입니다.");
            }

            // 중복 시간 체크를 위한 Set
            Set<String> timeSet = new HashSet<>();

            for (String time : times) {
                if (time == null || time.trim().isEmpty()) {
                    throw new BadRequestException(day + " 요일에 빈 시간 데이터가 있습니다.");
                }

                // 시간 형식 검증
                if (!TIME_PATTERN.matcher(time.trim()).matches()) {
                    throw new BadRequestException(day + " 요일의 시간 형식이 올바르지 않습니다: " + time +
                            " (HH:mm 형식, 30분 단위만 허용)");
                }

                // 중복 시간 체크
                if (!timeSet.add(time.trim())) {
                    throw new BadRequestException(day + " 요일에 중복된 시간이 있습니다: " + time);
                }
            }
        }
    }
}
