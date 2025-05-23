package com.jandi.band_backend.user.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.security.CustomUserDetails;
import com.jandi.band_backend.user.dto.MyClubRespDTO;
import com.jandi.band_backend.user.dto.MyTeamRespDTO;
import com.jandi.band_backend.user.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "MyPage API", description = "마이페이지 관련 API")
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(
        summary = "내가 참가한 동아리 목록 조회",
        description = "현재 로그인한 사용자가 참가한 모든 동아리 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "내가 참가한 동아리 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/clubs")
    public ApiResponse<List<MyClubRespDTO>> getMyClubs(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<MyClubRespDTO> myClubs = myPageService.getMyClubs(currentUser.getUserId());
        return ApiResponse.success("내가 참가한 동아리 목록 조회 성공", myClubs);
    }

    @Operation(
        summary = "내가 참가한 팀 목록 조회",
        description = "현재 로그인한 사용자가 참가한 모든 팀 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "내가 참가한 팀 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/teams")
    public ApiResponse<List<MyTeamRespDTO>> getMyTeams(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<MyTeamRespDTO> myTeams = myPageService.getMyTeams(currentUser.getUserId());
        return ApiResponse.success("내가 참가한 팀 목록 조회 성공", myTeams);
    }
} 