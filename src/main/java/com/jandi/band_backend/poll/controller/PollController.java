package com.jandi.band_backend.poll.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.poll.dto.*;
import com.jandi.band_backend.poll.service.PollService;
import com.jandi.band_backend.security.CustomUserDetails;
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

@Tag(name = "Poll API", description = "투표 관리 API")
@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    @Operation(
        summary = "투표 생성",
        description = "새로운 투표를 생성합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "투표 생성 성공",
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
    @PostMapping
    public ResponseEntity<ApiResponse<PollRespDTO>> createPoll(
            @Parameter(description = "투표 생성 요청 정보", required = true)
            @Valid @RequestBody PollReqDTO requestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PollRespDTO responseDto = pollService.createPoll(requestDto, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("투표가 성공적으로 생성되었습니다.", responseDto));
    }

    @Operation(
        summary = "클럽별 투표 목록 조회",
        description = "특정 클럽의 투표 목록을 페이지네이션으로 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "투표 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "클럽을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/clubs/{clubId}")
    public ResponseEntity<ApiResponse<Page<PollRespDTO>>> getPollList(
            @Parameter(description = "클럽 ID", required = true, example = "1")
            @PathVariable Integer clubId,
            @Parameter(description = "페이지네이션 정보 (기본 크기: 5)", required = false)
            @PageableDefault(size = 5) Pageable pageable) {

        Page<PollRespDTO> polls = pollService.getPollsByClub(clubId, pageable);
        return ResponseEntity.ok(ApiResponse.success("투표 목록을 조회했습니다.", polls));
    }

    @Operation(
        summary = "투표 상세 조회",
        description = "특정 투표의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "투표 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "투표를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/{pollId}")
    public ResponseEntity<ApiResponse<PollDetailRespDTO>> getPollDetail(
            @Parameter(description = "투표 ID", required = true, example = "1")
            @PathVariable Integer pollId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer currentUserId = userDetails != null ? userDetails.getUserId() : null;
        PollDetailRespDTO responseDto = pollService.getPollDetail(pollId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("투표 상세 정보를 조회했습니다.", responseDto));
    }

    @Operation(
        summary = "투표에 곡 추가",
        description = "특정 투표에 새로운 곡을 추가합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "곡 추가 성공",
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
            responseCode = "404",
            description = "투표를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PostMapping("/{pollId}/songs")
    public ResponseEntity<ApiResponse<PollSongRespDTO>> addSongToPoll(
            @Parameter(description = "투표 ID", required = true, example = "1")
            @PathVariable Integer pollId,
            @Parameter(description = "곡 추가 요청 정보", required = true)
            @Valid @RequestBody PollSongReqDTO requestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PollSongRespDTO responseDto = pollService.addSongToPoll(pollId, requestDto, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("곡이 성공적으로 투표에 추가되었습니다.", responseDto));
    }

    @Operation(
        summary = "곡에 투표하기",
        description = "특정 곡에 이모지로 투표합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "투표 설정 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "투표 또는 곡을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PutMapping("/{pollId}/songs/{songId}/votes/{emoji}")
    public ResponseEntity<ApiResponse<PollSongRespDTO>> setVoteForSong(
            @Parameter(description = "투표 ID", required = true, example = "1")
            @PathVariable Integer pollId,
            @Parameter(description = "곡 ID", required = true, example = "1")
            @PathVariable Integer songId,
            @Parameter(description = "투표 이모지", required = true, example = "👍")
            @PathVariable String emoji,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PollSongRespDTO responseDto = pollService.setVoteForSong(pollId, songId, emoji, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("투표가 설정되었습니다.", responseDto));
    }

    @Operation(
        summary = "곡 투표 취소",
        description = "특정 곡에 대한 투표를 취소합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "투표 취소 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "투표 또는 곡을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @DeleteMapping("/{pollId}/songs/{songId}/votes/{emoji}")
    public ResponseEntity<ApiResponse<PollSongRespDTO>> removeVoteFromSong(
            @Parameter(description = "투표 ID", required = true, example = "1")
            @PathVariable Integer pollId,
            @Parameter(description = "곡 ID", required = true, example = "1")
            @PathVariable Integer songId,
            @Parameter(description = "취소할 투표 이모지", required = true, example = "👍")
            @PathVariable String emoji,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PollSongRespDTO responseDto = pollService.removeVoteFromSong(pollId, songId, emoji, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("투표가 취소되었습니다.", responseDto));
    }
}
