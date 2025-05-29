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
import io.swagger.v3.oas.annotations.Parameter;
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
    @GetMapping
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> getPromos(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준 (예: createdAt,desc)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        Page<PromoRespDTO> promoPage = promoService.getPromos(userId, pageable);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 목록 조회 성공",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(
        summary = "공연 홍보 상세 조회", 
        description = "특정 공연 홍보의 상세 정보를 조회합니다. 조회 시 조회수가 1 증가합니다."
    )
    @GetMapping("/{promoId}")
    public ResponseEntity<CommonRespDTO<PromoRespDTO>> getPromo(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 상세 조회 성공",
                promoService.getPromo(promoId, userId)));
    }

    @Operation(
        summary = "공연 홍보 생성", 
        description = "새로운 공연 홍보를 생성합니다. teamName은 필수이고, 이미지는 1개까지 업로드할 수 있습니다."
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonRespDTO<PromoSimpleRespDTO>> createPromo(
            @Parameter(description = "팀명", example = "락밴드 팀", required = true) @RequestParam String teamName,
            @Parameter(description = "공연 제목", example = "락밴드 정기공연", required = true) @RequestParam String title,
            @Parameter(description = "입장료", example = "10000") @RequestParam(required = false) Integer admissionFee,
            @Parameter(description = "공연 일시 (ISO 8601)", example = "2024-03-15T19:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDatetime,
            @Parameter(description = "공연 장소", example = "홍대 클럽") @RequestParam(required = false) String location,
            @Parameter(description = "상세 주소", example = "서울시 마포구 홍익로 123") @RequestParam(required = false) String address,
            @Parameter(description = "공연 설명", example = "락밴드 팀의 정기 공연입니다.") @RequestParam(required = false) String description,
            @Parameter(description = "공연 이미지 파일") @RequestParam(value = "image", required = false) MultipartFile image,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // DTO 객체 생성
        PromoReqDTO request = new PromoReqDTO();
        request.setTeamName(teamName);
        request.setTitle(title);
        request.setAdmissionFee(admissionFee != null ? new BigDecimal(admissionFee) : null);
        request.setEventDatetime(eventDatetime);
        request.setLocation(location);
        request.setAddress(address);
        request.setDescription(description);
        request.setImage(image);
        
        Integer userId = userDetails.getUserId();
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 생성 성공!",
                promoService.createPromo(request, userId)));
    }

    @Operation(
        summary = "공연 홍보 수정", 
        description = "기존 공연 홍보를 수정합니다. 작성자만 수정할 수 있습니다. 전송된 필드만 수정되고 나머지는 기존 값을 유지합니다."
    )
    @PatchMapping(value = "/{promoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonRespDTO<Void>> updatePromo(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(description = "팀명", example = "수정된 팀명") @RequestParam(required = false) String teamName,
            @Parameter(description = "공연 제목", example = "수정된 공연 제목") @RequestParam(required = false) String title,
            @Parameter(description = "입장료", example = "12000") @RequestParam(required = false) Integer admissionFee,
            @Parameter(description = "공연 일시 (ISO 8601)", example = "2024-03-15T19:30:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDatetime,
            @Parameter(description = "공연 장소", example = "새로운 장소") @RequestParam(required = false) String location,
            @Parameter(description = "상세 주소", example = "새로운 주소") @RequestParam(required = false) String address,
            @Parameter(description = "공연 설명", example = "수정된 공연 설명") @RequestParam(required = false) String description,
            @Parameter(description = "새 이미지 파일 (기존 이미지 교체)") @RequestParam(value = "image", required = false) MultipartFile image,
            @Parameter(description = "삭제할 이미지 URL") @RequestParam(value = "deleteImageUrl", required = false) String deleteImageUrl,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // DTO 객체 생성
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

    @Operation(
        summary = "공연 홍보 삭제", 
        description = "공연 홍보를 삭제합니다. 작성자만 삭제할 수 있습니다. (소프트 삭제)"
    )
    @DeleteMapping("/{promoId}")
    public ResponseEntity<CommonRespDTO<Void>> deletePromo(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        promoService.deletePromo(promoId, userId);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 삭제 성공!", null));
    }

    @Operation(
        summary = "공연 홍보 검색", 
        description = "키워드로 공연 홍보를 검색합니다. 제목, 설명, 장소 등에서 검색됩니다."
    )
    @GetMapping("/search")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> searchPromos(
            @Parameter(description = "검색 키워드", example = "락밴드") @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준 (예: createdAt,desc)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        Page<PromoRespDTO> promoPage = promoService.searchPromos(keyword, userId, pageable);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 검색 성공",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(
        summary = "공연 홍보 필터링", 
        description = "다양한 조건으로 공연 홍보를 필터링합니다."
    )
    @GetMapping("/filter")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> filterPromos(
            @Parameter(description = "시작 날짜", example = "2024-03-01T00:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "종료 날짜", example = "2024-03-31T23:59:59") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "팀명", example = "락밴드") @RequestParam(required = false) String teamName,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준 (예: createdAt,desc)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        Page<PromoRespDTO> promoPage = promoService.filterPromos(startDate, endDate, teamName, userId, pageable);
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 필터링 성공",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(
        summary = "공연 홍보 좋아요 추가/취소", 
        description = "공연 홍보에 좋아요를 추가하거나 취소합니다. 토글 방식으로 동작합니다."
    )
    @PostMapping("/{promoId}/like")
    public ResponseEntity<CommonRespDTO<String>> togglePromoLike(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoLikeService.togglePromoLike(promoId, userId);
        
        String message = isLiked ? "공연 홍보 좋아요가 추가되었습니다." : "공연 홍보 좋아요가 취소되었습니다.";
        String result = isLiked ? "liked" : "unliked";
        
        return ResponseEntity.ok(CommonRespDTO.success(message, result));
    }

    @Operation(
        summary = "공연 홍보 좋아요 상태 확인", 
        description = "현재 사용자가 해당 공연 홍보에 좋아요를 눌렀는지 확인합니다."
    )
    @GetMapping("/{promoId}/like/status")
    public ResponseEntity<CommonRespDTO<Boolean>> getPromoLikeStatus(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        boolean isLiked = promoLikeService.isLikedByUser(promoId, userId);
        
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 좋아요 상태 조회 성공", isLiked));
    }

    @Operation(
        summary = "공연 홍보 좋아요 수 조회", 
        description = "해당 공연 홍보의 총 좋아요 수를 조회합니다."
    )
    @GetMapping("/{promoId}/like/count")
    public ResponseEntity<CommonRespDTO<Integer>> getPromoLikeCount(
            @Parameter(description = "공연 홍보 ID", example = "1") @PathVariable Integer promoId) {
        Integer likeCount = promoLikeService.getLikeCount(promoId);
        
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 좋아요 수 조회 성공", likeCount));
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