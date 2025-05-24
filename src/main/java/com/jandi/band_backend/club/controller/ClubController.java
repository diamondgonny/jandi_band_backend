package com.jandi.band_backend.club.controller;

import com.jandi.band_backend.club.dto.ClubReqDTO;
import com.jandi.band_backend.club.dto.ClubDetailRespDTO;
import com.jandi.band_backend.club.dto.ClubRespDTO;
import com.jandi.band_backend.club.dto.ClubUpdateReqDTO;
import com.jandi.band_backend.club.service.ClubService;
import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Club API")
@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @Operation(summary = "동아리 생성")
    @PostMapping
    public ResponseEntity<CommonResponse<ClubDetailRespDTO>> createClub(
            @Valid @RequestBody ClubReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        ClubDetailRespDTO response = clubService.createClub(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success("동아리가 성공적으로 생성되었습니다", response));
    }

    @Operation(summary = "동아리 목록 조회")
    @GetMapping
    public ResponseEntity<CommonResponse<Page<ClubRespDTO>>> getClubList(
            @PageableDefault(size = 5) Pageable pageable) {
        Page<ClubRespDTO> response = clubService.getClubList(pageable);
        return ResponseEntity.ok(CommonResponse.success("동아리 목록 조회 성공", response));
    }

    @Operation(summary = "동아리 상세 조회")
    @GetMapping("/{clubId}")
    public ResponseEntity<CommonResponse<ClubDetailRespDTO>> getClubDetail(@PathVariable Integer clubId) {
        ClubDetailRespDTO response = clubService.getClubDetail(clubId);
        return ResponseEntity.ok(CommonResponse.success("동아리 상세 정보 조회 성공", response));
    }

    @Operation(summary = "동아리 정보 수정")
    @PatchMapping("/{clubId}")
    public ResponseEntity<CommonResponse<ClubDetailRespDTO>> updateClub(
            @PathVariable Integer clubId,
            @Valid @RequestBody ClubUpdateReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        ClubDetailRespDTO response = clubService.updateClub(clubId, request, userId);
        return ResponseEntity.ok(CommonResponse.success("동아리 정보가 성공적으로 수정되었습니다", response));
    }

    @Operation(summary = "동아리 삭제")
    @DeleteMapping("/{clubId}")
    public ResponseEntity<CommonResponse<Void>> deleteClub(
            @PathVariable Integer clubId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        clubService.deleteClub(clubId, userId);
        return ResponseEntity.ok(CommonResponse.success("동아리가 성공적으로 삭제되었습니다"));
    }
}
