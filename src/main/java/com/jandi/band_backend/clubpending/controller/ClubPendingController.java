package com.jandi.band_backend.clubpending.controller;

import com.jandi.band_backend.clubpending.dto.*;
import com.jandi.band_backend.clubpending.service.ClubPendingService;
import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ClubPending API", description = "동아리 가입 신청 관리 API")
@RestController
@RequestMapping("/api/clubs/pending")
@RequiredArgsConstructor
public class ClubPendingController {
    
    private final ClubPendingService clubPendingService;
    
    @Operation(summary = "동아리 가입 신청", description = "사용자가 동아리에 가입 신청을 합니다.")
    @PostMapping("/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonRespDTO<ClubPendingRespDTO> applyToClub(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ClubPendingApplyReqDTO reqDTO) {
        
        ClubPendingRespDTO respDTO = clubPendingService.applyToClub(userDetails.getUserId(), reqDTO);
        return new CommonRespDTO<>(HttpStatus.CREATED.value(), "가입 신청이 완료되었습니다.", respDTO);
    }
    
    @Operation(summary = "동아리 대기 목록 조회", description = "동아리장이 대기중인 가입 신청 목록을 조회합니다.")
    @GetMapping("/club/{clubId}")
    public CommonRespDTO<ClubPendingListRespDTO> getPendingListByClub(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer clubId) {
        
        ClubPendingListRespDTO respDTO = clubPendingService.getPendingListByClub(clubId, userDetails.getUserId());
        return new CommonRespDTO<>(HttpStatus.OK.value(), "대기 목록 조회 성공", respDTO);
    }
    
    @Operation(summary = "내 신청 목록 조회", description = "사용자가 본인의 가입 신청 목록을 조회합니다.")
    @GetMapping("/my")
    public CommonRespDTO<UserPendingListRespDTO> getMyPendingList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        UserPendingListRespDTO respDTO = clubPendingService.getUserPendingList(userDetails.getUserId());
        return new CommonRespDTO<>(HttpStatus.OK.value(), "신청 목록 조회 성공", respDTO);
    }
    
    @Operation(summary = "가입 신청 승인/거부", description = "동아리장이 가입 신청을 승인하거나 거부합니다.")
    @PatchMapping("/{pendingId}/process")
    public CommonRespDTO<ClubPendingRespDTO> processPending(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer pendingId,
            @Valid @RequestBody ClubPendingProcessReqDTO reqDTO) {
        
        ClubPendingRespDTO respDTO = clubPendingService.processPending(pendingId, userDetails.getUserId(), reqDTO);
        String message = reqDTO.getApprove() ? "가입 신청이 승인되었습니다." : "가입 신청이 거부되었습니다.";
        return new CommonRespDTO<>(HttpStatus.OK.value(), message, respDTO);
    }
    
    @Operation(summary = "가입 신청 취소", description = "사용자가 본인의 가입 신청을 취소합니다.")
    @DeleteMapping("/{pendingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonRespDTO<Void> cancelPending(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer pendingId) {
        
        clubPendingService.cancelPending(pendingId, userDetails.getUserId());
        return new CommonRespDTO<>(HttpStatus.NO_CONTENT.value(), "가입 신청이 취소되었습니다.", null);
    }
}