package com.jandi.band_backend.promo.controller;

import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.promo.dto.PromoReportReqDTO;
import com.jandi.band_backend.promo.dto.PromoReportRespDTO;
import com.jandi.band_backend.promo.service.PromoReportService;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Promo Report API")
@RestController
@RequestMapping("/api/promos/reports")
@RequiredArgsConstructor
public class PromoReportController {

    private final PromoReportService promoReportService;

    @Operation(summary = "공연 홍보 신고 생성")
    @PostMapping
    public ResponseEntity<CommonRespDTO<Void>> createPromoReport(
            @RequestBody PromoReportReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer reporterUserId = userDetails.getUserId();
        promoReportService.createPromoReport(request, reporterUserId);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 신고 성공!", null));
    }

}
