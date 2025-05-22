package com.jandi.band_backend.user.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import com.jandi.band_backend.user.dto.UserTimetableListRespDTO;
import com.jandi.band_backend.user.dto.UserTimetableReqDTO;
import com.jandi.band_backend.user.dto.UserTimetableRespDTO;
import com.jandi.band_backend.user.service.UserTimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserTimetableController {
    private final UserTimetableService userTimetableService;
    private final JwtTokenProvider jwtTokenProvider;

    /// 내 시간표 목록 조회
    @GetMapping("/me/timetables")
    public ApiResponse<List<UserTimetableListRespDTO>> getMyTimetables(
        @RequestHeader("Authorization") String token
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        List<UserTimetableListRespDTO> myTimetables = userTimetableService.getMyTimetables(kakaoOauthId);
        return ApiResponse.success("내 시간표 목록 조회 성공", myTimetables);
    }

    /// 내 특정 시간표 조회
    @GetMapping("me/timetables/{timetableId}")
    public ApiResponse<UserTimetableRespDTO> getTimetableById(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer timetableId
    ){
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        UserTimetableRespDTO myTimetable = userTimetableService.getMyTimetableById(kakaoOauthId, timetableId);
        return ApiResponse.success("내 시간표 조회 성공", myTimetable);
    }

    /// 새 시간표 생성
    @PostMapping("/me/timetables")
    public ApiResponse<UserTimetableRespDTO> createTimetable(
            @RequestHeader("Authorization") String token,
            @RequestBody UserTimetableReqDTO userTimetableReqDTO
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        UserTimetableRespDTO newTimetable = userTimetableService.createTimetable(kakaoOauthId, userTimetableReqDTO);
        return ApiResponse.success("새 시간표 생성 성공, ", newTimetable);
    }

    /// 내 시간표 수정
    @PatchMapping("/me/timetables/{timetableId}")
    public ApiResponse<UserTimetableRespDTO> updateTimetable(
            @RequestHeader("Authorization") String token,
            @RequestBody UserTimetableReqDTO userTimetableReqDTO,
            @PathVariable Integer timetableId
    ){
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        UserTimetableRespDTO updateTimetable = userTimetableService.updateTimetable(kakaoOauthId, timetableId, userTimetableReqDTO);
        return ApiResponse.success("내 시간표 수정 성공", updateTimetable);
    }

    /// 내 시간표 삭제
    @DeleteMapping("/me/timetables/{timetableId}")
    public ApiResponse<?> deleteTimetable(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer timetableId
    ){
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        userTimetableService.deleteMyTimetable(kakaoOauthId, timetableId);
        return ApiResponse.success("내 시간표 삭제 성공");
    }
}
