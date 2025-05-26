package com.jandi.band_backend.promo.controller;

import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.promo.dto.PromoReqDTO;
import com.jandi.band_backend.promo.dto.PromoRespDTO;
import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.service.PromoService;
import com.jandi.band_backend.promo.service.PromoLikeService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Tag(name = "Promo API", description = "공연 홍보 관련 API")
@RestController
@RequestMapping("/api/promos")
@RequiredArgsConstructor
public class PromoController {

    private final PromoService promoService;
    private final PromoLikeService promoLikeService;

    @Operation(
        summary = "공연 홍보 목록 조회", 
        description = "모든 공연 홍보 목록을 페이지네이션으로 조회합니다. 로그인한 사용자의 경우 좋아요 상태도 함께 반환됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping
    public ResponseEntity<CommonResponse<Page<PromoRespDTO>>> getPromos(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준 (예: createdAt,desc)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 목록 조회 성공", 
                promoService.getPromos(userId, pageable)));
    }

    @Operation(
        summary = "클럽별 공연 홍보 목록 조회", 
        description = "특정 클럽의 공연 홍보 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "클럽을 찾을 수 없음")
    })
    @GetMapping("/club/{clubId}")
    public ResponseEntity<CommonResponse<Page<PromoRespDTO>>> getPromosByClub(
            @Parameter(description = "클럽 ID", example = "1") @PathVariable Integer clubId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준 (예: createdAt,desc)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        return ResponseEntity.ok(CommonResponse.success("클럽별 공연 홍보 목록 조회 성공",
                promoService.getPromosByClub(clubId, userId, pageable)));
    }

    @Operation(
        summary = "공연 홍보 상세 조회", 
        description = "특정 공연 홍보의 상세 정보를 조회합니다. 조회 시 조회수가 1 증가합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "공연 홍보를 찾을 수 없음")
    })
    @GetMapping("/{promoId}")
    public ResponseEntity<CommonResponse<PromoRespDTO>> getPromo(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 상세 조회 성공",
                promoService.getPromo(promoId, userId)));
    }

    @Operation(
        summary = "공연 홍보 생성", 
        description = "새로운 공연 홍보를 생성합니다. 클럽 멤버만 생성할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "클럽 멤버가 아님"),
        @ApiResponse(responseCode = "404", description = "클럽을 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<CommonResponse<PromoRespDTO>> createPromo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "공연 홍보 생성 정보",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PromoReqDTO.class),
                    examples = @ExampleObject(
                        name = "공연 홍보 생성 예시",
                        value = """
                        {
                          "clubId": 1,
                          "title": "락밴드 동아리 정기공연",
                          "admissionFee": 10000,
                          "eventDatetime": "2024-03-15T19:00:00",
                          "location": "홍대 클럽",
                          "address": "서울시 마포구 홍익로 123",
                          "description": "락밴드 동아리의 정기 공연입니다. 다양한 장르의 음악을 선보일 예정입니다.",
                          "status": "UPCOMING"
                        }
                        """
                    )
                )
            )
            @Valid @RequestBody PromoReqDTO request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 생성 성공",
                promoService.createPromo(request, userId)));
    }

    @Operation(
        summary = "공연 홍보 수정", 
        description = "기존 공연 홍보를 수정합니다. 작성자만 수정할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
        @ApiResponse(responseCode = "404", description = "공연 홍보를 찾을 수 없음")
    })
    @PutMapping("/{promoId}")
    public ResponseEntity<CommonResponse<PromoRespDTO>> updatePromo(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "공연 홍보 수정 정보",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PromoReqDTO.class),
                    examples = @ExampleObject(
                        name = "공연 홍보 수정 예시",
                        value = """
                        {
                          "clubId": 1,
                          "title": "수정된 공연 제목",
                          "admissionFee": 12000,
                          "eventDatetime": "2024-03-15T19:30:00",
                          "location": "새로운 장소",
                          "address": "새로운 주소",
                          "description": "수정된 공연 설명",
                          "status": "ONGOING"
                        }
                        """
                    )
                )
            )
            @Valid @RequestBody PromoReqDTO request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 수정 성공",
                promoService.updatePromo(promoId, request, userId)));
    }

    @Operation(
        summary = "공연 홍보 삭제", 
        description = "공연 홍보를 삭제합니다. 작성자만 삭제할 수 있습니다. (소프트 삭제)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
        @ApiResponse(responseCode = "404", description = "공연 홍보를 찾을 수 없음")
    })
    @DeleteMapping("/{promoId}")
    public ResponseEntity<CommonResponse<Void>> deletePromo(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        promoService.deletePromo(promoId, userId);
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 삭제 성공", null));
    }

    @Operation(
        summary = "공연 홍보 이미지 업로드", 
        description = "공연 홍보에 이미지를 업로드합니다. 작성자만 업로드할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일 형식"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "업로드 권한 없음"),
        @ApiResponse(responseCode = "404", description = "공연 홍보를 찾을 수 없음")
    })
    @PostMapping(value = "/{promoId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse<String>> uploadPromoImage(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(description = "업로드할 이미지 파일 (JPG, PNG 등)") @RequestParam("image") MultipartFile image,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        Integer userId = userDetails.getUserId();
        String imageUrl = promoService.uploadPromoImage(promoId, image, userId);
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 이미지 업로드 성공", imageUrl));
    }

    @Operation(
        summary = "공연 홍보 이미지 삭제", 
        description = "공연 홍보의 특정 이미지를 삭제합니다. 작성자만 삭제할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
        @ApiResponse(responseCode = "404", description = "공연 홍보 또는 이미지를 찾을 수 없음")
    })
    @DeleteMapping("/{promoId}/images")
    public ResponseEntity<CommonResponse<Void>> deletePromoImage(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(description = "삭제할 이미지 URL", example = "https://example.com/image.jpg") @RequestParam String imageUrl,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        promoService.deletePromoImage(promoId, imageUrl, userId);
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 이미지 삭제 성공", null));
    }

    @Operation(
        summary = "공연 홍보 검색", 
        description = "키워드로 공연 홍보를 검색합니다. 제목, 설명, 장소 등에서 검색됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 검색 키워드")
    })
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<Page<PromoRespDTO>>> searchPromos(
            @Parameter(description = "검색 키워드", example = "락밴드") @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준 (예: createdAt,desc)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 검색 성공",
                promoService.searchPromos(keyword, userId, pageable)));
    }

    @Operation(
        summary = "공연 홍보 필터링", 
        description = "다양한 조건으로 공연 홍보를 필터링합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "필터링 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 필터 조건")
    })
    @GetMapping("/filter")
    public ResponseEntity<CommonResponse<Page<PromoRespDTO>>> filterPromos(
            @Parameter(description = "공연 상태", example = "UPCOMING") @RequestParam(required = false) Promo.PromoStatus status,
            @Parameter(description = "시작 날짜", example = "2024-03-01T00:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "종료 날짜", example = "2024-03-31T23:59:59") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "클럽 ID", example = "1") @RequestParam(required = false) Integer clubId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준 (예: createdAt,desc)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 필터링 성공",
                promoService.filterPromos(status, startDate, endDate, clubId, userId, pageable)));
    }

    @Operation(
        summary = "공연 홍보 좋아요 추가/취소", 
        description = "공연 홍보에 좋아요를 추가하거나 취소합니다. 토글 방식으로 동작합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 처리 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "404", description = "공연 홍보를 찾을 수 없음")
    })
    @PostMapping("/{promoId}/like")
    public ResponseEntity<CommonResponse<String>> togglePromoLike(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoLikeService.togglePromoLike(promoId, userId);
        
        String message = isLiked ? "공연 홍보 좋아요가 추가되었습니다." : "공연 홍보 좋아요가 취소되었습니다.";
        String result = isLiked ? "liked" : "unliked";
        
        return ResponseEntity.ok(CommonResponse.success(message, result));
    }

    @Operation(
        summary = "공연 홍보 좋아요 상태 확인", 
        description = "현재 사용자가 해당 공연 홍보에 좋아요를 눌렀는지 확인합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상태 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "404", description = "공연 홍보를 찾을 수 없음")
    })
    @GetMapping("/{promoId}/like/status")
    public ResponseEntity<CommonResponse<Boolean>> getPromoLikeStatus(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoLikeService.isLikedByUser(promoId, userId);
        
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 좋아요 상태 조회 성공", isLiked));
    }

    @Operation(
        summary = "공연 홍보 좋아요 수 조회", 
        description = "해당 공연 홍보의 총 좋아요 수를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 수 조회 성공"),
        @ApiResponse(responseCode = "404", description = "공연 홍보를 찾을 수 없음")
    })
    @GetMapping("/{promoId}/like/count")
    public ResponseEntity<CommonResponse<Integer>> getPromoLikeCount(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId) {
        Integer likeCount = promoLikeService.getLikeCount(promoId);
        
        return ResponseEntity.ok(CommonResponse.success("공연 홍보 좋아요 수 조회 성공", likeCount));
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