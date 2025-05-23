package com.jandi.band_backend.team.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.security.CustomUserDetails;
import com.jandi.band_backend.team.dto.TeamDetailRespDTO;
import com.jandi.band_backend.team.dto.TeamReqDTO;
import com.jandi.band_backend.team.dto.TeamRespDTO;
import com.jandi.band_backend.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Team API", description = "팀 관리 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(
        summary = "팀 생성",
        description = "동아리 내에서 새로운 곡 팀을 생성합니다. 팀 생성자는 자동으로 팀장이 됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "팀 생성 성공",
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
            description = "권한 없음 (동아리 멤버가 아님)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "동아리를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PostMapping("/clubs/{clubId}/teams")
    public ResponseEntity<ApiResponse<TeamDetailRespDTO>> createTeam(
            @Parameter(description = "동아리 ID", required = true, example = "1")
            @PathVariable Integer clubId,
            @Parameter(description = "팀 생성 요청 정보", required = true)
            @Valid @RequestBody TeamReqDTO teamReqDTO,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer currentUserId = userDetails.getUserId();
        TeamDetailRespDTO result = teamService.createTeam(clubId, teamReqDTO, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("곡 팀이 성공적으로 생성되었습니다.", result));
    }

    @Operation(
        summary = "동아리 팀 목록 조회",
        description = "특정 동아리에 속한 팀들의 목록을 페이지네이션으로 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "팀 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "동아리를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/clubs/{clubId}/teams")
    public ResponseEntity<ApiResponse<Page<TeamRespDTO>>> getTeamsByClub(
            @Parameter(description = "동아리 ID", required = true, example = "1")
            @PathVariable Integer clubId,
            @Parameter(description = "페이지네이션 정보 (page=0부터 시작, size=5 기본값)", required = false)
            @PageableDefault(size = 5) Pageable pageable,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer currentUserId = userDetails.getUserId();
        Page<TeamRespDTO> result = teamService.getTeamsByClub(clubId, pageable, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("곡 팀 목록을 성공적으로 조회했습니다.", result));
    }

    @Operation(
        summary = "팀 상세 정보 조회",
        description = "특정 팀의 상세 정보를 조회합니다. 팀 멤버와 팀 상세 정보를 포함합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "팀 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (동아리 멤버가 아님)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "팀을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/teams/{teamId}")
    public ResponseEntity<ApiResponse<TeamDetailRespDTO>> getTeamDetail(
            @Parameter(description = "팀 ID", required = true, example = "1")
            @PathVariable Integer teamId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer currentUserId = userDetails.getUserId();
        TeamDetailRespDTO result = teamService.getTeamDetail(teamId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("곡 팀 정보를 성공적으로 조회했습니다.", result));
    }

    @Operation(
        summary = "팀 정보 수정",
        description = "팀의 정보를 수정합니다. 팀장만 수정할 수 있습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "팀 정보 수정 성공",
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
            description = "권한 없음 (팀장이 아님)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "팀을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PatchMapping("/teams/{teamId}")
    public ResponseEntity<ApiResponse<TeamDetailRespDTO>> updateTeam(
            @Parameter(description = "팀 ID", required = true, example = "1")
            @PathVariable Integer teamId,
            @Parameter(description = "팀 수정 요청 정보", required = true)
            @Valid @RequestBody TeamReqDTO teamReqDTO,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer currentUserId = userDetails.getUserId();
        TeamDetailRespDTO result = teamService.updateTeam(teamId, teamReqDTO, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("곡 팀 정보가 성공적으로 수정되었습니다.", result));
    }

    @Operation(
        summary = "팀 삭제",
        description = "팀을 삭제합니다. 팀장만 삭제할 수 있습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "팀 삭제 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (팀장이 아님)",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "팀을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @DeleteMapping("/teams/{teamId}")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(
            @Parameter(description = "팀 ID", required = true, example = "1")
            @PathVariable Integer teamId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer currentUserId = userDetails.getUserId();
        teamService.deleteTeam(teamId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("곡 팀이 성공적으로 삭제되었습니다."));
    }
}
