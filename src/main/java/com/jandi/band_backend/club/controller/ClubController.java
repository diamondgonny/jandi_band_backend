package com.jandi.band_backend.club.controller;

import com.jandi.band_backend.club.dto.ClubReqDTO;
import com.jandi.band_backend.club.dto.ClubDetailRespDTO;
import com.jandi.band_backend.club.dto.ClubRespDTO;
import com.jandi.band_backend.club.dto.ClubUpdateReqDTO;
import com.jandi.band_backend.club.service.ClubService;
import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.security.CustomUserDetails;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "Club API", description = "동아리 관리 API")
@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @Operation(
        summary = "동아리 생성",
        description = "새로운 동아리를 생성합니다. " +
                     "universityId가 null이면 연합 동아리로, 값이 있으면 특정 대학 소속 동아리로 생성됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "동아리 생성 성공",
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
    public ResponseEntity<ApiResponse<ClubDetailRespDTO>> createClub(
            @Parameter(description = "동아리 생성 요청 정보", required = true)
            @Valid @RequestBody ClubReqDTO request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        ClubDetailRespDTO response = clubService.createClub(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("동아리가 성공적으로 생성되었습니다", response));
    }

    @Operation(
        summary = "동아리 목록 조회",
        description = "페이지네이션을 지원하는 동아리 목록을 반환합니다. " +
                     "응답에는 각 동아리가 연합 동아리인지 여부(isUnionClub)와 소속 대학(있는 경우)이 포함됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "동아리 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ClubRespDTO>>> getClubList(
            @Parameter(description = "페이지네이션 정보 (기본 크기: 5)", required = false)
            @PageableDefault(size = 5) Pageable pageable) {
        Page<ClubRespDTO> response = clubService.getClubList(pageable);
        return ResponseEntity.ok(ApiResponse.success("동아리 목록 조회 성공", response));
    }

    @Operation(
        summary = "동아리 상세 조회",
        description = "특정 동아리의 상세 정보를 조회합니다. " +
                     "연합 동아리인 경우 university 필드는 null이고 isUnionClub은 true입니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "동아리 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "동아리를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/{clubId}")
    public ResponseEntity<ApiResponse<ClubDetailRespDTO>> getClubDetail(
            @Parameter(description = "동아리 ID", required = true, example = "1")
            @PathVariable Integer clubId) {
        ClubDetailRespDTO response = clubService.getClubDetail(clubId);
        return ResponseEntity.ok(ApiResponse.success("동아리 상세 정보 조회 성공", response));
    }

    @Operation(
        summary = "동아리 정보 수정",
        description = "대표자가 동아리 정보를 수정합니다. " +
                     "universityId를 변경하여 소속 대학을 변경하거나, null로 설정하여 연합 동아리로 변경할 수 있습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "동아리 정보 수정 성공",
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
            description = "동아리 수정 권한 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "동아리를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PatchMapping("/{clubId}")
    public ResponseEntity<ApiResponse<ClubDetailRespDTO>> updateClub(
            @Parameter(description = "동아리 ID", required = true, example = "1")
            @PathVariable Integer clubId,
            @Parameter(description = "동아리 수정 요청 정보", required = true)
            @Valid @RequestBody ClubUpdateReqDTO request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        ClubDetailRespDTO response = clubService.updateClub(clubId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("동아리 정보가 성공적으로 수정되었습니다", response));
    }

    @Operation(
        summary = "동아리 삭제",
        description = "대표자가 동아리를 삭제합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "동아리 삭제 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "동아리 삭제 권한 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "동아리를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @DeleteMapping("/{clubId}")
    public ResponseEntity<ApiResponse<Void>> deleteClub(
            @Parameter(description = "동아리 ID", required = true, example = "1")
            @PathVariable Integer clubId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        clubService.deleteClub(clubId, userId);
        return ResponseEntity.ok(ApiResponse.success("동아리가 성공적으로 삭제되었습니다"));
    }
}
