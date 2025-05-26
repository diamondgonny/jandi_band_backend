package com.jandi.band_backend.promo.controller;

import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.global.dto.PagedResponse;
import com.jandi.band_backend.promo.dto.PromoCommentReqDTO;
import com.jandi.band_backend.promo.dto.PromoCommentRespDTO;
import com.jandi.band_backend.promo.service.PromoCommentService;
import com.jandi.band_backend.promo.service.PromoCommentLikeService;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@Tag(name = "Promo Comment API", description = "공연 홍보 댓글 관련 API")
@RestController
@RequestMapping("/api/promos")
@RequiredArgsConstructor
public class PromoCommentController {

    private final PromoCommentService promoCommentService;
    private final PromoCommentLikeService promoCommentLikeService;

    @Operation(
        summary = "공연 홍보 댓글 목록 조회", 
        description = "특정 공연 홍보의 댓글 목록을 페이지네이션으로 조회합니다. 로그인한 사용자의 경우 댓글 좋아요 상태도 함께 반환됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "공연 홍보를 찾을 수 없음")
    })
    @GetMapping("/{promoId}/comments")
    public ResponseEntity<CommonResponse<PagedResponse<PromoCommentRespDTO>>> getCommentsByPromo(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준 (예: createdAt,desc)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        Page<PromoCommentRespDTO> commentPage = promoCommentService.getCommentsByPromo(promoId, userId, pageable);
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 댓글 목록을 조회했습니다.",
                PagedResponse.from(commentPage)));
    }

    @Operation(
        summary = "공연 홍보 댓글 생성", 
        description = "특정 공연 홍보에 새로운 댓글을 작성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "댓글 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "404", description = "공연 홍보를 찾을 수 없음")
    })
    @PostMapping("/{promoId}/comments")
    public ResponseEntity<CommonResponse<PromoCommentRespDTO>> createComment(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "댓글 생성 정보",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PromoCommentReqDTO.class),
                    examples = @ExampleObject(
                        name = "댓글 생성 예시",
                        value = """
                        {
                          "description": "정말 기대되는 공연이네요! 꼭 보러 가겠습니다."
                        }
                        """
                    )
                )
            )
            @Valid @RequestBody PromoCommentReqDTO request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success("공연 홍보 댓글이 성공적으로 생성되었습니다.",
                        promoCommentService.createComment(promoId, request, userId)));
    }

    @Operation(
        summary = "공연 홍보 댓글 수정", 
        description = "기존 댓글을 수정합니다. 작성자만 수정할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponse<PromoCommentRespDTO>> updateComment(
            @Parameter(description = "댓글 ID", example = "1") @PathVariable Integer commentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "댓글 수정 정보",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PromoCommentReqDTO.class),
                    examples = @ExampleObject(
                        name = "댓글 수정 예시",
                        value = """
                        {
                          "description": "수정된 댓글 내용입니다. 더욱 기대됩니다!"
                        }
                        """
                    )
                )
            )
            @Valid @RequestBody PromoCommentReqDTO request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 댓글이 성공적으로 수정되었습니다.",
                promoCommentService.updateComment(commentId, request, userId)));
    }

    @Operation(
        summary = "공연 홍보 댓글 삭제", 
        description = "댓글을 삭제합니다. 작성자만 삭제할 수 있습니다. (소프트 삭제)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponse<Void>> deleteComment(
            @Parameter(description = "댓글 ID", example = "1") @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        promoCommentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 댓글이 성공적으로 삭제되었습니다.", null));
    }

    @Operation(
        summary = "공연 홍보 댓글 좋아요 추가/취소", 
        description = "댓글에 좋아요를 추가하거나 취소합니다. 토글 방식으로 동작합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 처리 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<CommonResponse<String>> toggleCommentLike(
            @Parameter(description = "댓글 ID", example = "1") @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoCommentLikeService.togglePromoCommentLike(commentId, userId);
        
        String message = isLiked ? "댓글 좋아요가 추가되었습니다." : "댓글 좋아요가 취소되었습니다.";
        String result = isLiked ? "liked" : "unliked";
        
        return ResponseEntity.ok(CommonResponse.success(message, result));
    }

    @Operation(
        summary = "공연 홍보 댓글 좋아요 상태 확인", 
        description = "현재 사용자가 해당 댓글에 좋아요를 눌렀는지 확인합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상태 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @GetMapping("/comments/{commentId}/like/status")
    public ResponseEntity<CommonResponse<Boolean>> getCommentLikeStatus(
            @Parameter(description = "댓글 ID", example = "1") @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoCommentLikeService.isLikedByUser(commentId, userId);
        
        return ResponseEntity.ok(CommonResponse.success("댓글 좋아요 상태 조회 성공", isLiked));
    }

    @Operation(
        summary = "공연 홍보 댓글 좋아요 수 조회", 
        description = "해당 댓글의 총 좋아요 수를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 수 조회 성공"),
        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @GetMapping("/comments/{commentId}/like/count")
    public ResponseEntity<CommonResponse<Integer>> getCommentLikeCount(
            @Parameter(description = "댓글 ID", example = "1") @PathVariable Integer commentId) {
        Integer likeCount = promoCommentLikeService.getLikeCount(commentId);
        
        return ResponseEntity.ok(CommonResponse.success("댓글 좋아요 수 조회 성공", likeCount));
    }

    // Pageable 생성 헬퍼 메서드
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