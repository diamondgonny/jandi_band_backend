package com.jandi.band_backend.club.controller;

import com.jandi.band_backend.club.dto.ClubEventReqDTO;
import com.jandi.band_backend.club.dto.ClubEventRespDTO;
import com.jandi.band_backend.club.service.ClubEventService;
import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs/{clubId}")
@RequiredArgsConstructor
public class ClubEventController {

    private final ClubEventService clubEventService;

    // 동아리 일정 추가 API
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

    // 동아리 일정 상세 조회 API
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

    // 동아리 일정 목록 조회 API
    @GetMapping("/events/list/{year}/{month}")
    public ResponseEntity<CommonRespDTO<List<ClubEventRespDTO>>> getClubEventsByMonth(
            @PathVariable Integer clubId,
            @PathVariable int year,
            @PathVariable int month,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getUserId();

        List<ClubEventRespDTO> response = clubEventService.getClubEventListByMonth(clubId, userId, year, month);

        return ResponseEntity.ok(CommonRespDTO.success("동아리 일정 목록 조회 성공", response));
    }

    // 동아리 일정 삭제 API
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