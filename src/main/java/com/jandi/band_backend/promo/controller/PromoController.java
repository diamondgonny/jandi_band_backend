package com.jandi.band_backend.promo.controller;

import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.global.dto.PagedRespDTO;
import com.jandi.band_backend.promo.dto.PromoReqDTO;
import com.jandi.band_backend.promo.dto.PromoRespDTO;
import com.jandi.band_backend.promo.dto.PromoSimpleRespDTO;
import com.jandi.band_backend.promo.service.PromoService;
import com.jandi.band_backend.promo.service.PromoLikeService;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> getPromos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        Page<PromoRespDTO> promoPage = promoService.getPromos(userId, pageable);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 목록 조회 성공",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(summary = "공연 홍보 상세 조회")
    @GetMapping("/{promoId}")
    public ResponseEntity<CommonRespDTO<PromoRespDTO>> getPromo(
            @PathVariable Integer promoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 상세 조회 성공",
                promoService.getPromo(promoId, userId)));
    }

    @Operation(summary = "공연 홍보 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonRespDTO<PromoSimpleRespDTO>> createPromo(
            @RequestParam String teamName,
            @RequestParam String title,
            @RequestParam(required = false) Integer admissionFee,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDatetime,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        PromoReqDTO request = new PromoReqDTO();
        request.setTeamName(teamName);
        request.setTitle(title);
        request.setAdmissionFee(admissionFee != null ? new BigDecimal(admissionFee) : null);
        request.setEventDatetime(eventDatetime);
        request.setLocation(location);
        request.setAddress(address);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setDescription(description);
        request.setImage(image);
        
        Integer userId = userDetails.getUserId();
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 생성 성공!",
                promoService.createPromo(request, userId)));
    }

    @Operation(summary = "공연 홍보 수정")
    @PatchMapping(value = "/{promoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonRespDTO<Void>> updatePromo(
            @PathVariable Integer promoId,
            @RequestParam(required = false) String teamName,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer admissionFee,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDatetime,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "deleteImageUrl", required = false) String deleteImageUrl,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        PromoReqDTO request = new PromoReqDTO();
        request.setTeamName(teamName);
        request.setTitle(title);
        request.setAdmissionFee(admissionFee != null ? new BigDecimal(admissionFee) : null);
        request.setEventDatetime(eventDatetime);
        request.setLocation(location);
        request.setAddress(address);
        request.setDescription(description);
        request.setImage(image);
        request.setDeleteImageUrl(deleteImageUrl);
        
        Integer userId = userDetails.getUserId();
        promoService.updatePromo(promoId, request, userId);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 수정 성공!", null));
    }

    @Operation(summary = "공연 홍보 삭제")
    @DeleteMapping("/{promoId}")
    public ResponseEntity<CommonRespDTO<Void>> deletePromo(
            @PathVariable Integer promoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        promoService.deletePromo(promoId, userId);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 삭제 성공!", null));
    }

    @Operation(summary = "공연 홍보 검색")
    @GetMapping("/search")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> searchPromos(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        Page<PromoRespDTO> promoPage = promoService.searchPromos(keyword, userId, pageable);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 검색 성공",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(summary = "공연 홍보 필터링")
    @GetMapping("/filter")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> filterPromos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String teamName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        Page<PromoRespDTO> promoPage = promoService.filterPromos(startDate, endDate, teamName, userId, pageable);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 필터링 성공",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(summary = "공연 홍보 지도상 검색")
    @GetMapping("/map")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> filterMapPromos(
            @RequestParam BigDecimal startLatitude,
            @RequestParam BigDecimal startLongitude,
            @RequestParam BigDecimal endLatitude,
            @RequestParam BigDecimal endLongitude,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        Page<PromoRespDTO> promoPage = promoService.filterMapPromos(startLatitude, startLongitude, endLatitude, endLongitude, userId, pageable);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 필터링 성공",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(summary = "공연 홍보 좋아요 추가/취소")
    @PostMapping("/{promoId}/like")
    public ResponseEntity<CommonRespDTO<String>> togglePromoLike(
            @PathVariable Integer promoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoLikeService.togglePromoLike(promoId, userId);
        
        String message = isLiked ? "공연 홍보 좋아요가 추가되었습니다." : "공연 홍보 좋아요가 취소되었습니다.";
        String result = isLiked ? "liked" : "unliked";
        
        return ResponseEntity.ok(CommonRespDTO.success(message, result));
    }

    @Operation(summary = "공연 홍보 좋아요 상태 확인")
    @GetMapping("/{promoId}/like/status")
    public ResponseEntity<CommonRespDTO<Boolean>> getPromoLikeStatus(
            @PathVariable Integer promoId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoLikeService.isLikedByUser(promoId, userId);
        
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 좋아요 상태 조회 성공", isLiked));
    }

    @Operation(summary = "공연 홍보 좋아요 수 조회")
    @GetMapping("/{promoId}/like/count")
    public ResponseEntity<CommonRespDTO<Integer>> getPromoLikeCount(
            @PathVariable Integer promoId) {
        Integer likeCount = promoLikeService.getLikeCount(promoId);
        
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 좋아요 수 조회 성공", likeCount));
    }

    private Pageable createPageable(int page, int size, String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return PageRequest.of(page, size);
        }
        
        String[] sortParts = sort.split(",");
        String property = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1]) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        
        return PageRequest.of(page, size, Sort.by(direction, property));
    }
} 