package com.jandi.band_backend.notice.controller;

import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.global.dto.PagedRespDTO;
import com.jandi.band_backend.notice.dto.SiteNoticeReqDTO;
import com.jandi.band_backend.notice.dto.SiteNoticeDetailRespDTO;
import com.jandi.band_backend.notice.dto.SiteNoticeRespDTO;
import com.jandi.band_backend.notice.service.SiteNoticeService;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Site Notice API")
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class SiteNoticeController {

    private final SiteNoticeService siteNoticeService;

    @Operation(summary = "현재 활성 공지사항 조회 (팝업용)")
    @GetMapping("/active")
    public ResponseEntity<CommonRespDTO<List<SiteNoticeRespDTO>>> getActiveNotices() {
        List<SiteNoticeRespDTO> response = siteNoticeService.getActiveNotices();
        return ResponseEntity.ok(CommonRespDTO.success("활성 공지사항 조회 성공", response));
    }

    @Operation(summary = "공지사항 목록 조회 (관리자 전용)")
    @GetMapping
    public ResponseEntity<CommonRespDTO<PagedRespDTO<SiteNoticeRespDTO>>> getAllNotices(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Integer userId = userDetails.getUserId();
        Page<SiteNoticeRespDTO> response = siteNoticeService.getAllNotices(userId, pageable);
        return ResponseEntity.ok(CommonRespDTO.success("공지사항 목록 조회 성공", PagedRespDTO.from(response)));
    }

    @Operation(summary = "공지사항 상세 조회 (관리자 전용)")
    @GetMapping("/{noticeId}")
    public ResponseEntity<CommonRespDTO<SiteNoticeDetailRespDTO>> getNoticeDetail(
            @PathVariable Integer noticeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        SiteNoticeDetailRespDTO response = siteNoticeService.getNoticeDetail(noticeId, userId);
        return ResponseEntity.ok(CommonRespDTO.success("공지사항 상세 조회 성공", response));
    }

    @Operation(summary = "공지사항 생성 (관리자 전용)")
    @PostMapping
    public ResponseEntity<CommonRespDTO<SiteNoticeDetailRespDTO>> createNotice(
            @Valid @RequestBody SiteNoticeReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer creatorId = userDetails.getUserId();
        SiteNoticeDetailRespDTO response = siteNoticeService.createNotice(request, creatorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonRespDTO.success("공지사항이 성공적으로 생성되었습니다", response));
    }

    @Operation(summary = "공지사항 수정 (관리자 전용)")
    @PutMapping("/{noticeId}")
    public ResponseEntity<CommonRespDTO<SiteNoticeDetailRespDTO>> updateNotice(
            @PathVariable Integer noticeId,
            @Valid @RequestBody SiteNoticeReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        SiteNoticeDetailRespDTO response = siteNoticeService.updateNotice(noticeId, request, userId);
        return ResponseEntity.ok(CommonRespDTO.success("공지사항이 성공적으로 수정되었습니다", response));
    }

    @Operation(summary = "공지사항 삭제 (관리자 전용)")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<CommonRespDTO<Void>> deleteNotice(
            @PathVariable Integer noticeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        siteNoticeService.deleteNotice(noticeId, userId);
        return ResponseEntity.ok(CommonRespDTO.success("공지사항이 성공적으로 삭제되었습니다"));
    }

    @Operation(summary = "공지사항 일시정지/재개 토글 (관리자 전용)")
    @PatchMapping("/{noticeId}/toggle-pause")
    public ResponseEntity<CommonRespDTO<SiteNoticeRespDTO>> toggleNoticePause(
            @PathVariable Integer noticeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        SiteNoticeRespDTO response = siteNoticeService.toggleNoticeStatus(noticeId, userId);
        return ResponseEntity.ok(CommonRespDTO.success("공지사항 일시정지 상태가 성공적으로 변경되었습니다", response));
    }
}
