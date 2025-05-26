package com.jandi.band_backend.team.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jandi.band_backend.club.repository.ClubMemberRepository;
import com.jandi.band_backend.global.exception.BadRequestException;
import com.jandi.band_backend.global.exception.InvalidAccessException;
import com.jandi.band_backend.global.exception.ResourceNotFoundException;
import com.jandi.band_backend.global.exception.UnauthorizedClubAccessException;
import com.jandi.band_backend.team.dto.ScheduleSuggestionRespDTO;
import com.jandi.band_backend.team.dto.TimetableReqDTO;
import com.jandi.band_backend.team.dto.TimetableRespDTO;
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
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
