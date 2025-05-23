package com.jandi.band_backend.team.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.team.dto.PracticeScheduleReqDTO;
import com.jandi.band_backend.team.dto.PracticeScheduleRespDTO;
import com.jandi.band_backend.team.service.PracticeScheduleService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Practice Schedule API", description = "연습 일정 관리 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PracticeScheduleController {

    private final PracticeScheduleService practiceScheduleService;

    @Operation(
        summary = "팀별 연습 일정 목록 조회",
        description = "특정 팀의 모든 연습 일정을 페이지네이션으로 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "팀별 곡 연습 일정 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "팀을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/teams/{teamId}/practice-schedules")
    public ResponseEntity<ApiResponse<Page<PracticeScheduleRespDTO>>> getPracticeSchedulesByTeam(
            @Parameter(description = "팀 ID", required = true, example = "1")
            @PathVariable Integer teamId,
            @Parameter(description = "페이지네이션 정보", required = false)
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("팀별 곡 연습 일정 목록 조회 성공", 
                practiceScheduleService.getPracticeSchedulesByTeam(teamId, pageable)));
    }

    @Operation(
        summary = "연습 일정 상세 조회",
        description = "특정 연습 일정의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "곡 연습 일정 상세 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "연습 일정을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/practice-schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<PracticeScheduleRespDTO>> getPracticeSchedule(
            @Parameter(description = "연습 일정 ID", required = true, example = "1")
            @PathVariable Integer scheduleId) {
        return ResponseEntity.ok(ApiResponse.success("곡 연습 일정 상세 조회 성공", 
                practiceScheduleService.getPracticeSchedule(scheduleId)));
    }

    @Operation(
        summary = "연습 일정 생성",
        description = "특정 팀에 새로운 연습 일정을 생성합니다. 팀 리더만 생성할 수 있습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "곡 연습 일정 생성 성공",
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
            description = "연습 일정 생성 권한 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "팀을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PostMapping("/teams/{teamId}/practice-schedules")
    public ResponseEntity<ApiResponse<PracticeScheduleRespDTO>> createPracticeSchedule(
            @Parameter(description = "팀 ID", required = true, example = "1")
            @PathVariable Integer teamId,
            @Parameter(description = "연습 일정 생성 요청 정보", required = true)
            @Valid @RequestBody PracticeScheduleReqDTO request,
            @Parameter(hidden = true)
            @RequestAttribute("userId") Integer userId) {
        // teamId를 request에 설정
        request.setTeamId(teamId);
        
        return ResponseEntity.ok(ApiResponse.success("곡 연습 일정 생성 성공", 
                practiceScheduleService.createPracticeSchedule(request, userId)));
    }

    @Operation(
        summary = "연습 일정 삭제",
        description = "특정 연습 일정을 삭제합니다. 일정 생성자나 팀 리더만 삭제할 수 있습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "곡 연습 일정 삭제 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "연습 일정 삭제 권한 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "연습 일정을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @DeleteMapping("/practice-schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> deletePracticeSchedule(
            @Parameter(description = "연습 일정 ID", required = true, example = "1")
            @PathVariable Integer scheduleId,
            @Parameter(hidden = true)
            @RequestAttribute("userId") Integer userId) {
        practiceScheduleService.deletePracticeSchedule(scheduleId, userId);
        return ResponseEntity.ok(ApiResponse.success("곡 연습 일정 삭제 성공"));
    }
}