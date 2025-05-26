package com.jandi.band_backend.team.controller;

import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.security.CustomUserDetails;
import com.jandi.band_backend.team.dto.ScheduleSuggestionRespDTO;
import com.jandi.band_backend.team.dto.TimetableReqDTO;
import com.jandi.band_backend.team.dto.TimetableRespDTO;
import com.jandi.band_backend.team.service.TeamTimetableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Team Timetable API")
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamTimetableController {

    private final TeamTimetableService teamTimetableService;

    @Operation(summary = "팀내 스케줄 조율 제안")
    @PostMapping("/{teamId}/schedule-suggestion")
    public ResponseEntity<CommonResponse<ScheduleSuggestionRespDTO>> startScheduleSuggestion(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer currentUserId = userDetails.getUserId();
        ScheduleSuggestionRespDTO result = teamTimetableService.startScheduleSuggestion(teamId, currentUserId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("스케줄 조율 모드가 시작되었습니다", result));
    }

    @Operation(summary = "팀내 내 시간표 수정")
    @PatchMapping("/{teamId}/members/me/timetable")
    public ResponseEntity<CommonResponse<TimetableRespDTO>> submitMyTimetable(
            @PathVariable Integer teamId,
            @Valid @RequestBody TimetableReqDTO reqDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer currentUserId = userDetails.getUserId();
        TimetableRespDTO result = teamTimetableService.submitMyTimetable(teamId, reqDTO, currentUserId);
        return ResponseEntity.ok(CommonResponse.success("팀 시간표 수정 성공", result));
    }
}
