package com.jandi.band_backend.club.controller;

import com.jandi.band_backend.club.dto.CalendarEventRespDTO;
import com.jandi.band_backend.club.dto.ClubEventReqDTO;
import com.jandi.band_backend.club.dto.ClubEventRespDTO;
import com.jandi.band_backend.club.service.ClubEventService;
import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Club Event API")
@RestController
@RequestMapping("/api/clubs/{clubId}")
@RequiredArgsConstructor
public class ClubEventController {

    private final ClubEventService clubEventService;

    @Operation(summary = "동아리 일정 추가")
    @PostMapping("/events")
    public ResponseEntity<CommonRespDTO<ClubEventRespDTO>> createClubEvent(
            @PathVariable Integer clubId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ClubEventReqDTO dto
    ) {
        Integer userId = userDetails.getUserId();
        ClubEventRespDTO response = clubEventService.createClubEvent(clubId, userId, dto);

        return ResponseEntity.ok(CommonRespDTO.success("동아리 일정이 생성되었습니다.", response));
    }

    @Operation(summary = "동아리 일정 상세 조회")
    @GetMapping("/events/{eventId}")
    public ResponseEntity<CommonRespDTO<ClubEventRespDTO>> getClubEventDetail(
            @PathVariable Integer clubId,
            @PathVariable Integer eventId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getUserId();

        ClubEventRespDTO response = clubEventService.getClubEventDetail(clubId, eventId, userId);

        return ResponseEntity.ok(CommonRespDTO.success("동아리 일정 상세 조회 성공", response));
    }

    @Operation(summary = "캘린더용 통합 일정 조회 (동아리 일정 + 하위 팀 일정)")
    @GetMapping("/calendar")
    public ResponseEntity<CommonRespDTO<List<CalendarEventRespDTO>>> getCalendarEvents(
            @PathVariable Integer clubId,
            @Parameter(description = "조회할 연도 (예: 2024)") @RequestParam int year,
            @Parameter(description = "조회할 월 (1-12)") @RequestParam int month,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getUserId();

        List<CalendarEventRespDTO> response = clubEventService.getCalendarEventsForClub(clubId, userId, year, month);

        return ResponseEntity.ok(CommonRespDTO.success("캘린더 일정 조회 성공", response));
    }

    @Operation(summary = "동아리 일정 삭제")
    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<CommonRespDTO<Void>> deleteClubEvent(
            @PathVariable Integer clubId,
            @PathVariable Integer eventId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getUserId();

        clubEventService.deleteClubEvent(clubId, eventId, userId);

        return ResponseEntity.ok(CommonRespDTO.success("동아리 일정이 삭제되었습니다."));
    }
}
