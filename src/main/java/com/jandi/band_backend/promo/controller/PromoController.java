package com.jandi.band_backend.promo.controller;

import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.promo.dto.PromoReqDTO;
import com.jandi.band_backend.promo.dto.PromoRespDTO;
import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.service.PromoService;
import com.jandi.band_backend.promo.service.PromoLikeService;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Tag(name = "Promo API")
@RestController
@RequestMapping("/api/promos")
@RequiredArgsConstructor
public class PromoController {

    private final PromoService promoService;
    private final PromoLikeService promoLikeService;

    @Operation(summary = "공연 홍보 목록 조회")
    @GetMapping
    public ResponseEntity<CommonResponse<Page<PromoRespDTO>>> getPromos(Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 목록 조회 성공", promoService.getPromos(pageable)));
    }

    @Operation(summary = "클럽별 공연 홍보 목록 조회")
    @GetMapping("/club/{clubId}")
    public ResponseEntity<CommonResponse<Page<PromoRespDTO>>> getPromosByClub(
            @PathVariable Integer clubId,
            Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success("클럽별 공연 홍보 목록 조회 성공",
                promoService.getPromosByClub(clubId, pageable)));
    }

    @Operation(summary = "공연 홍보 상세 조회")
    @GetMapping("/{promoId}")
    public ResponseEntity<CommonResponse<PromoRespDTO>> getPromo(
            @PathVariable Integer promoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 상세 조회 성공",
                promoService.getPromo(promoId, userId)));
    }

    @Operation(summary = "공연 홍보 생성")
    @PostMapping
    public ResponseEntity<CommonResponse<PromoRespDTO>> createPromo(
            @Valid @RequestBody PromoReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 생성 성공",
                promoService.createPromo(request, userId)));
    }

    @Operation(summary = "공연 홍보 수정")
    @PutMapping("/{promoId}")
    public ResponseEntity<CommonResponse<PromoRespDTO>> updatePromo(
            @PathVariable Integer promoId,
            @Valid @RequestBody PromoReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 수정 성공",
                promoService.updatePromo(promoId, request, userId)));
    }

    @Operation(summary = "공연 홍보 삭제")
    @DeleteMapping("/{promoId}")
    public ResponseEntity<CommonResponse<Void>> deletePromo(
            @PathVariable Integer promoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        promoService.deletePromo(promoId, userId);
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 삭제 성공", null));
    }

    @Operation(summary = "공연 홍보 이미지 업로드")
    @PostMapping(value = "/{promoId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<String>> uploadPromoImage(
            @PathVariable Integer promoId,
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        Integer userId = userDetails.getUserId();
        String imageUrl = promoService.uploadPromoImage(promoId, image, userId);
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 이미지 업로드 성공", imageUrl));
    }

    @Operation(summary = "공연 홍보 이미지 삭제")
    @DeleteMapping("/{promoId}/images")
    public ResponseEntity<CommonResponse<Void>> deletePromoImage(
            @PathVariable Integer promoId,
            @RequestParam String imageUrl,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        promoService.deletePromoImage(promoId, imageUrl, userId);
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 이미지 삭제 성공", null));
    }

    @Operation(summary = "공연 홍보 검색")
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<Page<PromoRespDTO>>> searchPromos(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 검색 성공",
                promoService.searchPromos(keyword, pageable)));
    }

    @Operation(summary = "공연 홍보 필터링")
    @GetMapping("/filter")
    public ResponseEntity<CommonResponse<Page<PromoRespDTO>>> filterPromos(
            @RequestParam(required = false) Promo.PromoStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer clubId,
            Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 필터링 성공",
                promoService.filterPromos(status, startDate, endDate, clubId, pageable)));
    }

    @Operation(summary = "공연 홍보 좋아요 추가/취소")
    @PostMapping("/{promoId}/like")
    public ResponseEntity<CommonResponse<String>> togglePromoLike(
            @PathVariable Integer promoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoLikeService.togglePromoLike(promoId, userId);
        
        String message = isLiked ? "공연 홍보 좋아요가 추가되었습니다." : "공연 홍보 좋아요가 취소되었습니다.";
        String result = isLiked ? "liked" : "unliked";
        
        return ResponseEntity.ok(CommonResponse.success(message, result));
    }

    @Operation(summary = "공연 홍보 좋아요 상태 확인")
    @GetMapping("/{promoId}/like/status")
    public ResponseEntity<CommonResponse<Boolean>> getPromoLikeStatus(
            @PathVariable Integer promoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoLikeService.isLikedByUser(promoId, userId);
        
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 좋아요 상태 조회 성공", isLiked));
    }
} 