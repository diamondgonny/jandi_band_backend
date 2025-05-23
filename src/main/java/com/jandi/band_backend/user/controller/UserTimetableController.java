package com.jandi.band_backend.user.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import com.jandi.band_backend.user.dto.UserTimetableRespDTO;
import com.jandi.band_backend.user.dto.UserTimetableReqDTO;
import com.jandi.band_backend.user.dto.UserTimetableDetailsRespDTO;
import com.jandi.band_backend.user.service.UserTimetableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Timetable API", description = "사용자 시간표 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserTimetableController {
    private final UserTimetableService userTimetableService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "내 시간표 목록 조회")
    @GetMapping("/me/timetables")
    public ApiResponse<List<UserTimetableRespDTO>> getMyTimetables(
        @Parameter(description = "Bearer 토큰") @RequestHeader("Authorization") String token
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        List<UserTimetableRespDTO> myTimetables = userTimetableService.getMyTimetables(kakaoOauthId);
        return ApiResponse.success("내 시간표 목록 조회 성공", myTimetables);
    }

    @Operation(summary = "내 특정 시간표 조회")
    @GetMapping("me/timetables/{timetableId}")
    public ApiResponse<UserTimetableDetailsRespDTO> getTimetableById(
            @Parameter(description = "Bearer 토큰") @RequestHeader("Authorization") String token,
            @Parameter(description = "시간표 ID") @PathVariable Integer timetableId
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        UserTimetableDetailsRespDTO myTimetable = userTimetableService.getMyTimetableById(kakaoOauthId, timetableId);
        return ApiResponse.success("내 시간표 조회 성공", myTimetable);
    }

    @Operation(summary = "시간표 생성")
    @PostMapping("/me/timetables")
    public ApiResponse<UserTimetableDetailsRespDTO> createTimetable(
            @Parameter(description = "Bearer 토큰") @RequestHeader("Authorization") String token,
            @RequestBody UserTimetableReqDTO userTimetableReqDTO
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        UserTimetableDetailsRespDTO createdTimetable = userTimetableService.createTimetable(kakaoOauthId, userTimetableReqDTO);
        return ApiResponse.success("시간표 생성 성공", createdTimetable);
    }

    @Operation(summary = "시간표 수정")
    @PatchMapping("/me/timetables/{timetableId}")
    public ApiResponse<UserTimetableDetailsRespDTO> updateTimetable(
            @Parameter(description = "Bearer 토큰") @RequestHeader("Authorization") String token,
            @Parameter(description = "시간표 ID") @PathVariable Integer timetableId,
            @RequestBody UserTimetableReqDTO userTimetableReqDTO
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        UserTimetableDetailsRespDTO updatedTimetable = userTimetableService.updateTimetable(kakaoOauthId, timetableId, userTimetableReqDTO);
        return ApiResponse.success("시간표 수정 성공", updatedTimetable);
    }

    @Operation(summary = "시간표 삭제")
    @DeleteMapping("/me/timetables/{timetableId}")
    public ApiResponse<Void> deleteTimetable(
            @Parameter(description = "Bearer 토큰") @RequestHeader("Authorization") String token,
            @Parameter(description = "시간표 ID") @PathVariable Integer timetableId
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        userTimetableService.deleteMyTimetable(kakaoOauthId, timetableId);
        return ApiResponse.success("시간표 삭제 성공");
    }
}
