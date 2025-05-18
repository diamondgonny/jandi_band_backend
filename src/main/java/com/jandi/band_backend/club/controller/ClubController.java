package com.jandi.band_backend.club.controller;

import com.jandi.band_backend.club.dto.ClubReqDTO;
import com.jandi.band_backend.club.dto.ClubRespDTO;
import com.jandi.band_backend.club.dto.PageRespDTO;
import com.jandi.band_backend.club.service.ClubService;
import com.jandi.band_backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    /**
     * 동아리 추가 API
     * 사용자가 새로운 동아리를 생성합니다.
     * universityId가 null이면 연합 동아리로, 값이 있으면 특정 대학 소속 동아리로 생성됩니다.
     */
    @PostMapping
    public ResponseEntity<ClubRespDTO.Response> createClub(
            @Valid @RequestBody ClubReqDTO.Request request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        ClubRespDTO.Response response = clubService.createClub(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 동아리 목록 조회 API
     * 페이지네이션을 지원하는 동아리 목록을 반환합니다.
     * 응답에는 각 동아리가 연합 동아리인지 여부(isUnionClub)와 소속 대학(있는 경우)이 포함됩니다.
     */
    @GetMapping
    public ResponseEntity<PageRespDTO<ClubRespDTO.SimpleResponse>> getClubList(
            @PageableDefault(size = 5) Pageable pageable) {
        PageRespDTO<ClubRespDTO.SimpleResponse> response = clubService.getClubList(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 동아리 상세 조회 API
     * 특정 동아리의 상세 정보를 조회합니다.
     * 연합 동아리인 경우 university 필드는 null이고 isUnionClub은 true입니다.
     */
    @GetMapping("/{clubId}")
    public ResponseEntity<ClubRespDTO.Response> getClubDetail(
            @PathVariable Integer clubId) {
        ClubRespDTO.Response response = clubService.getClubDetail(clubId);
        return ResponseEntity.ok(response);
    }

    /**
     * 동아리 정보 수정 API
     * 대표자가 동아리 정보를 수정합니다.
     * universityId를 변경하여 소속 대학을 변경하거나, null로 설정하여 연합 동아리로 변경할 수 있습니다.
     */
    @PatchMapping("/{clubId}")
    public ResponseEntity<ClubRespDTO.Response> updateClub(
            @PathVariable Integer clubId,
            @Valid @RequestBody ClubReqDTO.UpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        ClubRespDTO.Response response = clubService.updateClub(clubId, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 동아리 삭제 API
     * 대표자가 동아리를 삭제합니다.
     */
    @DeleteMapping("/{clubId}")
    public ResponseEntity<Void> deleteClub(
            @PathVariable Integer clubId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        clubService.deleteClub(clubId, userId);
        return ResponseEntity.noContent().build();
    }
}
