package com.jandi.band_backend.club.controller;

import com.jandi.band_backend.club.dto.ClubEventReqDTO;
import com.jandi.band_backend.club.dto.ClubEventRespDTO;
import com.jandi.band_backend.club.service.ClubEventService;
import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs/{clubId}")
@RequiredArgsConstructor
public class ClubEventController {

    private final ClubEventService clubEventService;
    private final JwtTokenProvider jwtTokenProvider;

    // 동아리 일정 추가 API
    @PostMapping("/events")
    public ResponseEntity<CommonResponse<ClubEventRespDTO>> createClubEvent(
            @PathVariable Integer clubId,
            @RequestHeader("Authorization") String token,
            @RequestBody ClubEventReqDTO dto
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);
        ClubEventRespDTO response = clubEventService.createClubEvent(clubId, kakaoOauthId, dto);
        
        return ResponseEntity.ok(CommonResponse.success("동아리 일정이 생성되었습니다.", response));
    }

    // 동아리 일정 상세 조회 API
    @GetMapping("/events/{eventId}")
    public ResponseEntity<CommonResponse<ClubEventRespDTO>> getClubEventDetail(
            @PathVariable Integer clubId,
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String token
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        ClubEventRespDTO response = clubEventService.getClubEventDetail(clubId, eventId, kakaoOauthId);

        return ResponseEntity.ok(CommonResponse.success("동아리 일정 상세 조회 성공", response));
    }

    // 동아리 일정 목록 조회 API
    @GetMapping("/events/list/{year}/{month}")
    public ResponseEntity<CommonResponse<List<ClubEventRespDTO>>> getClubEventsByMonth(
            @PathVariable Integer clubId,
            @PathVariable int year,
            @PathVariable int month,
            @RequestHeader("Authorization") String token
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        List<ClubEventRespDTO> response = clubEventService.getClubEventListByMonth(clubId, kakaoOauthId, year, month);

        return ResponseEntity.ok(CommonResponse.success("동아리 일정 목록 조회 성공", response));
    }

    // 동아리 일정 삭제 API
    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<CommonResponse<Void>> deleteClubEvent(
            @PathVariable Integer clubId,
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String token
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        clubEventService.deleteClubEvent(clubId, eventId, kakaoOauthId);

        return ResponseEntity.ok(CommonResponse.success("동아리 일정이 삭제되었습니다."));
    }
}