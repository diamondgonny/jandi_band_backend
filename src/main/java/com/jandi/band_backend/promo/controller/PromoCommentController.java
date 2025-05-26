package com.jandi.band_backend.promo.controller;

import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.promo.dto.PromoCommentReqDTO;
import com.jandi.band_backend.promo.dto.PromoCommentRespDTO;
import com.jandi.band_backend.promo.service.PromoCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Promo Comment API")
@RestController
@RequestMapping("/api/promos")
@RequiredArgsConstructor
public class PromoCommentController {

    private final PromoCommentService promoCommentService;

    @Operation(summary = "공연 홍보 댓글 목록 조회")
    @GetMapping("/{promoId}/comments")
    public ResponseEntity<CommonResponse<Page<PromoCommentRespDTO>>> getCommentsByPromo(
            @PathVariable Integer promoId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 댓글 목록을 조회했습니다.",
                promoCommentService.getCommentsByPromo(promoId, pageable)));
    }

    @Operation(summary = "공연 홍보 댓글 생성")
    @PostMapping("/{promoId}/comments")
    public ResponseEntity<CommonResponse<PromoCommentRespDTO>> createComment(
            @PathVariable Integer promoId,
            @Valid @RequestBody PromoCommentReqDTO request,
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success("공연 홍보 댓글이 성공적으로 생성되었습니다.",
                        promoCommentService.createComment(promoId, request, userId)));
    }

    @Operation(summary = "공연 홍보 댓글 수정")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponse<PromoCommentRespDTO>> updateComment(
            @PathVariable Integer commentId,
            @Valid @RequestBody PromoCommentReqDTO request,
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 댓글이 성공적으로 수정되었습니다.",
                promoCommentService.updateComment(commentId, request, userId)));
    }

    @Operation(summary = "공연 홍보 댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponse<Void>> deleteComment(
            @PathVariable Integer commentId,
            @RequestAttribute("userId") Integer userId) {
        promoCommentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 댓글이 성공적으로 삭제되었습니다.", null));
    }
} 