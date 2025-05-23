package com.jandi.band_backend.team.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.team.dto.ClubTeamRespDTO;
import com.jandi.band_backend.team.service.ClubTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Club Team API", description = "동아리 팀 관리 API")
@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubTeamController {

    private final ClubTeamService clubTeamService;

    @Operation(
        summary = "동아리 팀 목록 조회",
        description = "특정 동아리에 속한 모든 팀 목록을 페이지네이션으로 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "동아리 팀 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "동아리를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/{clubId}/teams")
    public ApiResponse<Page<ClubTeamRespDTO>> getClubTeams(
            @Parameter(description = "동아리 ID", required = true, example = "1")
            @PathVariable Integer clubId,
            @Parameter(description = "페이지네이션 정보 (기본 크기: 10)", required = false)
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ClubTeamRespDTO> teams = clubTeamService.getTeamsByClub(clubId, pageable);
        return ApiResponse.success("동아리 팀 목록 조회 성공", teams);
    }
}