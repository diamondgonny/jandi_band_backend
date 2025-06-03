package com.jandi.band_backend.promo.controller;

import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.promo.dto.PromoCommentReportReqDTO;
import com.jandi.band_backend.promo.service.PromoCommentReportService;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Promo Comment Report API")
@RestController
@RequestMapping("/api/promos/comments/reports")
@RequiredArgsConstructor
public class PromoCommentReportController {

    private final PromoCommentReportService promoCommentReportService;

    @Operation(summary = "공연 홍보 댓글 신고 생성")
    @PostMapping
    public ResponseEntity<CommonRespDTO<Void>> createPromoCommentReport(
            @RequestBody PromoCommentReportReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer reporterUserId = userDetails.getUserId();
        promoCommentReportService.createPromoCommentReport(request, reporterUserId);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 댓글 신고 성공!", null));
    }
}