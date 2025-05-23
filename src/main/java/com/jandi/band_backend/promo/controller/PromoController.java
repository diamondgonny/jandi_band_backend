package com.jandi.band_backend.promo.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.promo.dto.PromoReqDTO;
import com.jandi.band_backend.promo.dto.PromoRespDTO;
import com.jandi.band_backend.promo.entity.Promo;
import com.jandi.band_backend.promo.service.PromoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Tag(name = "Promo API", description = "공연 홍보 관리 API")
@RestController
@RequestMapping("/api/promos")
@RequiredArgsConstructor
public class PromoController {

    private final PromoService promoService;

    @Operation(
        summary = "공연 홍보 목록 조회",
        description = "모든 공연 홍보 목록을 페이지네이션으로 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "공연 홍보 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PromoRespDTO>>> getPromos(
            @Parameter(description = "페이지네이션 정보", required = false)
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("공연 홍보 목록 조회 성공", promoService.getPromos(pageable)));
    }

    @Operation(
        summary = "클럽별 공연 홍보 목록 조회",
        description = "특정 클럽의 공연 홍보 목록을 페이지네이션으로 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "클럽별 공연 홍보 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "클럽을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/club/{clubId}")
    public ResponseEntity<ApiResponse<Page<PromoRespDTO>>> getPromosByClub(
            @Parameter(description = "클럽 ID", required = true, example = "1")
            @PathVariable Integer clubId,
            @Parameter(description = "페이지네이션 정보", required = false)
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("클럽별 공연 홍보 목록 조회 성공", 
                promoService.getPromosByClub(clubId, pageable)));
    }

    @Operation(
        summary = "공연 홍보 상세 조회",
        description = "특정 공연 홍보의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "공연 홍보 상세 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "공연 홍보를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/{promoId}")
    public ResponseEntity<ApiResponse<PromoRespDTO>> getPromo(
            @Parameter(description = "공연 홍보 ID", required = true, example = "1")
            @PathVariable Integer promoId) {
        return ResponseEntity.ok(ApiResponse.success("공연 홍보 상세 조회 성공", 
                promoService.getPromo(promoId)));
    }

    @Operation(
        summary = "공연 홍보 생성",
        description = "새로운 공연 홍보를 생성합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "공연 홍보 생성 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<PromoRespDTO>> createPromo(
            @Parameter(description = "공연 홍보 생성 요청 정보", required = true)
            @Valid @RequestBody PromoReqDTO request,
            @Parameter(hidden = true)
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(ApiResponse.success("공연 홍보 생성 성공", 
                promoService.createPromo(request, userId)));
    }

    @Operation(
        summary = "공연 홍보 수정",
        description = "기존 공연 홍보 정보를 수정합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "공연 홍보 수정 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "수정 권한 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "공연 홍보를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PutMapping("/{promoId}")
    public ResponseEntity<ApiResponse<PromoRespDTO>> updatePromo(
            @Parameter(description = "공연 홍보 ID", required = true, example = "1")
            @PathVariable Integer promoId,
            @Parameter(description = "공연 홍보 수정 요청 정보", required = true)
            @Valid @RequestBody PromoReqDTO request,
            @Parameter(hidden = true)
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(ApiResponse.success("공연 홍보 수정 성공", 
                promoService.updatePromo(promoId, request, userId)));
    }

    @Operation(
        summary = "공연 홍보 삭제",
        description = "공연 홍보를 삭제합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "공연 홍보 삭제 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "삭제 권한 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "공연 홍보를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @DeleteMapping("/{promoId}")
    public ResponseEntity<ApiResponse<Void>> deletePromo(
            @Parameter(description = "공연 홍보 ID", required = true, example = "1")
            @PathVariable Integer promoId,
            @Parameter(hidden = true)
            @RequestAttribute("userId") Integer userId) {
        promoService.deletePromo(promoId, userId);
        return ResponseEntity.ok(ApiResponse.success("공연 홍보 삭제 성공"));
    }

    @Operation(
        summary = "공연 홍보 이미지 업로드",
        description = "공연 홍보에 이미지를 업로드합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "공연 홍보 이미지 업로드 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 이미지 파일",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "업로드 권한 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "공연 홍보를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PostMapping(value = "/{promoId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadPromoImage(
            @Parameter(description = "공연 홍보 ID", required = true, example = "1")
            @PathVariable Integer promoId,
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("image") MultipartFile image,
            @Parameter(hidden = true)
            @RequestAttribute("userId") Integer userId) throws IOException {
        String imageUrl = promoService.uploadPromoImage(promoId, image, userId);
        return ResponseEntity.ok(ApiResponse.success("공연 홍보 이미지 업로드 성공", imageUrl));
    }

    @Operation(
        summary = "공연 홍보 이미지 삭제",
        description = "공연 홍보의 이미지를 삭제합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "공연 홍보 이미지 삭제 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "삭제 권한 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "공연 홍보 또는 이미지를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @DeleteMapping("/{promoId}/images")
    public ResponseEntity<ApiResponse<Void>> deletePromoImage(
            @Parameter(description = "공연 홍보 ID", required = true, example = "1")
            @PathVariable Integer promoId,
            @Parameter(description = "삭제할 이미지 URL", required = true)
            @RequestParam String imageUrl,
            @Parameter(hidden = true)
            @RequestAttribute("userId") Integer userId) {
        promoService.deletePromoImage(promoId, imageUrl, userId);
        return ResponseEntity.ok(ApiResponse.success("공연 홍보 이미지 삭제 성공"));
    }

    @Operation(
        summary = "공연 홍보 검색",
        description = "키워드를 통해 공연 홍보를 검색합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "공연 홍보 검색 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PromoRespDTO>>> searchPromos(
            @Parameter(description = "검색 키워드", required = true, example = "콘서트")
            @RequestParam String keyword,
            @Parameter(description = "페이지네이션 정보", required = false)
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("공연 홍보 검색 성공", 
                promoService.searchPromos(keyword, pageable)));
    }

    @Operation(
        summary = "공연 홍보 필터링",
        description = "다양한 조건으로 공연 홍보를 필터링합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "공연 홍보 필터링 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<PromoRespDTO>>> filterPromos(
            @Parameter(description = "공연 상태", required = false)
            @RequestParam(required = false) Promo.PromoStatus status,
            @Parameter(description = "시작 날짜", required = false, example = "2024-01-01T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "종료 날짜", required = false, example = "2024-12-31T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "클럽 ID", required = false, example = "1")
            @RequestParam(required = false) Integer clubId,
            @Parameter(description = "페이지네이션 정보", required = false)
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("공연 홍보 필터링 성공", 
                promoService.filterPromos(status, startDate, endDate, clubId, pageable)));
    }
} 