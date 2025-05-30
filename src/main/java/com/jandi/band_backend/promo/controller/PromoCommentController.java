package com.jandi.band_backend.promo.controller;

import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.global.dto.PagedRespDTO;
import com.jandi.band_backend.promo.dto.PromoCommentReqDTO;
import com.jandi.band_backend.promo.dto.PromoCommentRespDTO;
import com.jandi.band_backend.promo.service.PromoCommentService;
import com.jandi.band_backend.promo.service.PromoCommentLikeService;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Promo Comment API")
@RestController
@RequestMapping("/api/promos")
@RequiredArgsConstructor
public class PromoCommentController {

    private final PromoCommentService promoCommentService;
    private final PromoCommentLikeService promoCommentLikeService;

    @Operation(summary = "공연 홍보 댓글 목록 조회")
    @GetMapping("/{promoId}/comments")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoCommentRespDTO>>> getCommentsByPromo(
            @PathVariable Integer promoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        Page<PromoCommentRespDTO> commentPage = promoCommentService.getCommentsByPromo(promoId, userId, pageable);
                return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 댓글 목록을 조회했습니다.",
                PagedRespDTO.from(commentPage)));
    }

    @Operation(summary = "공연 홍보 댓글 생성")
    @PostMapping("/{promoId}/comments")
    public ResponseEntity<CommonRespDTO<PromoCommentRespDTO>> createComment(
            @PathVariable Integer promoId,
            @Valid @RequestBody PromoCommentReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonRespDTO.success("공연 홍보 댓글이 성공적으로 생성되었습니다.",
                        promoCommentService.createComment(promoId, request, userId)));
    }

    @Operation(summary = "공연 홍보 댓글 수정")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommonRespDTO<PromoCommentRespDTO>> updateComment(
            @PathVariable Integer commentId,
            @Valid @RequestBody PromoCommentReqDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 댓글이 성공적으로 수정되었습니다.",
                promoCommentService.updateComment(commentId, request, userId)));
    }

    @Operation(summary = "공연 홍보 댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommonRespDTO<Void>> deleteComment(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        promoCommentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 댓글이 성공적으로 삭제되었습니다.", null));
    }

    @Operation(summary = "공연 홍보 댓글 좋아요 추가/취소")
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<CommonRespDTO<String>> toggleCommentLike(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoCommentLikeService.togglePromoCommentLike(commentId, userId);
        
        String message = isLiked ? "댓글 좋아요가 추가되었습니다." : "댓글 좋아요가 취소되었습니다.";
        String result = isLiked ? "liked" : "unliked";
        
        return ResponseEntity.ok(CommonRespDTO.success(message, result));
    }

    @Operation(summary = "공연 홍보 댓글 좋아요 상태 확인")
    @GetMapping("/comments/{commentId}/like/status")
    public ResponseEntity<CommonRespDTO<Boolean>> getCommentLikeStatus(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoCommentLikeService.isLikedByUser(commentId, userId);
        
        return ResponseEntity.ok(CommonRespDTO.success("댓글 좋아요 상태 조회 성공", isLiked));
    }

    @Operation(summary = "공연 홍보 댓글 좋아요 수 조회")
    @GetMapping("/comments/{commentId}/like/count")
    public ResponseEntity<CommonRespDTO<Integer>> getCommentLikeCount(
            @PathVariable Integer commentId) {
        Integer likeCount = promoCommentLikeService.getLikeCount(commentId);
        
        return ResponseEntity.ok(CommonRespDTO.success("댓글 좋아요 수 조회 성공", likeCount));
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