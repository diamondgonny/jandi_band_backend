// ClubEventController.java
package com.jandi.band_backend.club.controller;

import com.jandi.band_backend.club.dto.ClubEventReqDTO;
import com.jandi.band_backend.club.dto.ClubEventRespDTO;
import com.jandi.band_backend.club.service.ClubEventService;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Tag(name = "Club Event API", description = "동아리 이벤트 관리 API")
@RestController
@RequestMapping("/api/clubs/{clubId}/events")
@RequiredArgsConstructor
public class ClubEventController {

    private final ClubEventService clubEventService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
        summary = "동아리 이벤트 생성",
        description = "특정 동아리에 새로운 이벤트를 생성합니다. 동아리 멤버만 이벤트를 생성할 수 있습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "동아리 이벤트 생성 성공",
            content = @Content(schema = @Schema(implementation = ClubEventRespDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ClubEventRespDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ClubEventRespDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "동아리 이벤트 생성 권한 없음",
            content = @Content(schema = @Schema(implementation = ClubEventRespDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "동아리를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ClubEventRespDTO.class))
        )
    })
    @PostMapping("/add")
    public ClubEventRespDTO createClubEvent(
            @Parameter(description = "동아리 ID", required = true, example = "1")
            @PathVariable Integer clubId,
            @Parameter(description = "Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "동아리 이벤트 생성 요청 정보", required = true)
            @RequestBody ClubEventReqDTO dto
    ) {
        String accessToken = token.replace("Bearer ", "");
        // 원본
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);
        // 예시 데이터
//        String kakaoOauthId = "4264188474";
        return clubEventService.createClubEvent(clubId, kakaoOauthId, dto);

    }
}