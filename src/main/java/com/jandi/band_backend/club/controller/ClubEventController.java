// ClubEventController.java
package com.jandi.band_backend.club.controller;

import com.jandi.band_backend.club.dto.ClubEventReqDTO;
import com.jandi.band_backend.club.dto.ClubEventRespDTO;
import com.jandi.band_backend.club.service.ClubEventService;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Club Event API")
@RestController
@RequestMapping("/api/clubs/{clubId}/events")
@RequiredArgsConstructor
public class ClubEventController {
    private final ClubEventService clubEventService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "동아리 이벤트 생성")
    @PostMapping("/add")
    public ClubEventRespDTO createClubEvent(
            @PathVariable Integer clubId,
            @RequestHeader("Authorization") String token,
            @RequestBody ClubEventReqDTO dto
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);
        return clubEventService.createClubEvent(clubId, kakaoOauthId, dto);
    }
}