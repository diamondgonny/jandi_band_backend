package com.jandi.band_backend.user.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import com.jandi.band_backend.user.dto.UserTimetableRespDTO;
import com.jandi.band_backend.user.dto.UserTimetableReqDTO;
import com.jandi.band_backend.user.dto.UserTimetableDetailsRespDTO;
import com.jandi.band_backend.user.service.UserTimetableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
        summary = "내 시간표 목록 조회",
        description = "로그인한 사용자의 모든 시간표 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "시간표 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/me/timetables")
    public ApiResponse<List<UserTimetableRespDTO>> getMyTimetables(
        @Parameter(description = "Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @RequestHeader("Authorization") String token
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        List<UserTimetableRespDTO> myTimetables = userTimetableService.getMyTimetables(kakaoOauthId);
        return ApiResponse.success("내 시간표 목록 조회 성공", myTimetables);
    }

    @Operation(
        summary = "내 특정 시간표 조회",
        description = "로그인한 사용자의 특정 시간표의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "시간표 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (본인의 시간표가 아님)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "시간표를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("me/timetables/{timetableId}")
    public ApiResponse<UserTimetableDetailsRespDTO> getTimetableById(
            @Parameter(description = "Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "시간표 ID", required = true, example = "1")
            @PathVariable Integer timetableId
    ){
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        UserTimetableDetailsRespDTO myTimetable = userTimetableService.getMyTimetableById(kakaoOauthId, timetableId);
        return ApiResponse.success("내 시간표 조회 성공", myTimetable);
    }

    @Operation(
        summary = "새 시간표 생성",
        description = "로그인한 사용자의 새로운 시간표를 생성합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "시간표 생성 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PostMapping("/me/timetables")
    public ApiResponse<UserTimetableDetailsRespDTO> createTimetable(
            @Parameter(description = "Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "시간표 생성 요청 정보", required = true)
            @RequestBody UserTimetableReqDTO userTimetableReqDTO
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        UserTimetableDetailsRespDTO newTimetable = userTimetableService.createTimetable(kakaoOauthId, userTimetableReqDTO);
        return ApiResponse.success("새 시간표 생성 성공, ", newTimetable);
    }

    @Operation(
        summary = "내 시간표 수정",
        description = "로그인한 사용자의 기존 시간표를 수정합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "시간표 수정 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (본인의 시간표가 아님)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "시간표를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PatchMapping("/me/timetables/{timetableId}")
    public ApiResponse<UserTimetableDetailsRespDTO> updateTimetable(
            @Parameter(description = "Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "시간표 수정 요청 정보", required = true)
            @RequestBody UserTimetableReqDTO userTimetableReqDTO,
            @Parameter(description = "시간표 ID", required = true, example = "1")
            @PathVariable Integer timetableId
    ){
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        UserTimetableDetailsRespDTO updateTimetable = userTimetableService.updateTimetable(kakaoOauthId, timetableId, userTimetableReqDTO);
        return ApiResponse.success("내 시간표 수정 성공", updateTimetable);
    }

    @Operation(
        summary = "내 시간표 삭제",
        description = "로그인한 사용자의 특정 시간표를 삭제합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "시간표 삭제 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (본인의 시간표가 아님)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "시간표를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @DeleteMapping("/me/timetables/{timetableId}")
    public ApiResponse<?> deleteTimetable(
            @Parameter(description = "Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "시간표 ID", required = true, example = "1")
            @PathVariable Integer timetableId
    ){
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        userTimetableService.deleteMyTimetable(kakaoOauthId, timetableId);
        return ApiResponse.success("내 시간표 삭제 성공");
    }
}
