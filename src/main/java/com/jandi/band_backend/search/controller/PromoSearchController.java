package com.jandi.band_backend.search.controller;

import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.global.dto.PagedRespDTO;
import com.jandi.band_backend.promo.dto.PromoRespDTO;
import com.jandi.band_backend.promo.service.PromoLikeService;
import com.jandi.band_backend.search.service.PromoSearchService;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "공연 홍보 검색 (Elasticsearch)", description = "Elasticsearch 기반 공연 홍보 검색 API")
@RestController
@RequestMapping("/api/promos")
@RequiredArgsConstructor
public class PromoSearchController {

    private final PromoSearchService promoSearchService;
    private final PromoLikeService promoLikeService;

    /*
    @Operation(summary = "공연 홍보 검색 (Elasticsearch 기반)")
    @GetMapping("/search-v2")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> searchPromos(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        
        // Elasticsearch에서 검색
        Page<com.jandi.band_backend.search.document.PromoDocument> promoDocuments = 
            promoSearchService.searchByKeyword(keyword, pageable);
        
        // PromoDocument를 PromoRespDTO로 변환 (사용자별 좋아요 상태 포함)
        List<PromoRespDTO> promoRespDTOs = promoDocuments.getContent().stream()
                .map(doc -> {
                    PromoRespDTO dto = convertToPromoRespDTO(doc);
                    // 사용자별 좋아요 상태 확인
                    if (userId != null) {
                        Boolean isLikedByUser = promoLikeService.isLikedByUser(Integer.valueOf(doc.getId()), userId);
                        dto.setIsLikedByUser(isLikedByUser);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        
        Page<PromoRespDTO> promoPage = new org.springframework.data.domain.PageImpl<>(
            promoRespDTOs, pageable, promoDocuments.getTotalElements());
        
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 검색 성공 (Elasticsearch)",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(summary = "제목으로 공연 홍보 검색")
    @GetMapping("/search-v2/title")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Elasticsearch에서 검색
        List<com.jandi.band_backend.search.document.PromoDocument> promoDocuments = 
            promoSearchService.searchByTitle(title);
        
        // 페이징 처리
        int start = page * size;
        int end = Math.min(start + size, promoDocuments.size());
        List<com.jandi.band_backend.search.document.PromoDocument> pagedDocuments = 
            promoDocuments.subList(start, end);
        
        List<PromoRespDTO> promoRespDTOs = pagedDocuments.stream()
                .map(this::convertToPromoRespDTO)
                .collect(Collectors.toList());
        
        Page<PromoRespDTO> promoPage = new org.springframework.data.domain.PageImpl<>(
            promoRespDTOs, PageRequest.of(page, size), promoDocuments.size());
        
        return ResponseEntity.ok(CommonRespDTO.success("제목 검색 성공",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(summary = "팀명으로 공연 홍보 검색")
    @GetMapping("/search-v2/team")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> searchByTeamName(
            @RequestParam String teamName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Elasticsearch에서 검색
        List<com.jandi.band_backend.search.document.PromoDocument> promoDocuments = 
            promoSearchService.searchByTeamName(teamName);
        
        // 페이징 처리
        int start = page * size;
        int end = Math.min(start + size, promoDocuments.size());
        List<com.jandi.band_backend.search.document.PromoDocument> pagedDocuments = 
            promoDocuments.subList(start, end);
        
        List<PromoRespDTO> promoRespDTOs = pagedDocuments.stream()
                .map(this::convertToPromoRespDTO)
                .collect(Collectors.toList());
        
        Page<PromoRespDTO> promoPage = new org.springframework.data.domain.PageImpl<>(
            promoRespDTOs, PageRequest.of(page, size), promoDocuments.size());
        
        return ResponseEntity.ok(CommonRespDTO.success("팀명 검색 성공",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(summary = "장소로 공연 홍보 검색")
    @GetMapping("/search-v2/location")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> searchByLocation(
            @RequestParam String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Elasticsearch에서 검색
        List<com.jandi.band_backend.search.document.PromoDocument> promoDocuments = 
            promoSearchService.searchByLocation(location);
        
        // 페이징 처리
        int start = page * size;
        int end = Math.min(start + size, promoDocuments.size());
        List<com.jandi.band_backend.search.document.PromoDocument> pagedDocuments = 
            promoDocuments.subList(start, end);
        
        List<PromoRespDTO> promoRespDTOs = pagedDocuments.stream()
                .map(this::convertToPromoRespDTO)
                .collect(Collectors.toList());
        
        Page<PromoRespDTO> promoPage = new org.springframework.data.domain.PageImpl<>(
            promoRespDTOs, PageRequest.of(page, size), promoDocuments.size());
        
        return ResponseEntity.ok(CommonRespDTO.success("장소 검색 성공",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(summary = "모든 공연 홍보 조회 (Elasticsearch)")
    @GetMapping("/search-v2/all")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> getAllPromos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // Elasticsearch에서 검색
        List<com.jandi.band_backend.search.document.PromoDocument> promoDocuments = 
            promoSearchService.findAll();
        
        // 페이징 처리
        int start = page * size;
        int end = Math.min(start + size, promoDocuments.size());
        List<com.jandi.band_backend.search.document.PromoDocument> pagedDocuments = 
            promoDocuments.subList(start, end);
        
        List<PromoRespDTO> promoRespDTOs = pagedDocuments.stream()
                .map(this::convertToPromoRespDTO)
                .collect(Collectors.toList());
        
        Page<PromoRespDTO> promoPage = new org.springframework.data.domain.PageImpl<>(
            promoRespDTOs, PageRequest.of(page, size), promoDocuments.size());
        
        return ResponseEntity.ok(CommonRespDTO.success("모든 공연 홍보 조회 성공",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(summary = "공연 홍보 필터링 (Elasticsearch 기반)")
    @GetMapping("/filter-v2")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> filterPromos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String teamName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        
        // Elasticsearch에서 필터링
        Page<com.jandi.band_backend.search.document.PromoDocument> promoDocuments = 
            promoSearchService.filterPromosByDateAndTeam(startDate, endDate, teamName, pageable);
        
        // PromoDocument를 PromoRespDTO로 변환 (사용자별 좋아요 상태 포함)
        List<PromoRespDTO> promoRespDTOs = promoDocuments.getContent().stream()
                .map(doc -> {
                    PromoRespDTO dto = convertToPromoRespDTO(doc);
                    // 사용자별 좋아요 상태 확인
                    if (userId != null) {
                        Boolean isLikedByUser = promoLikeService.isLikedByUser(Integer.valueOf(doc.getId()), userId);
                        dto.setIsLikedByUser(isLikedByUser);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        
        Page<PromoRespDTO> promoPage = new org.springframework.data.domain.PageImpl<>(
            promoRespDTOs, pageable, promoDocuments.getTotalElements());
        
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 필터링 성공 (Elasticsearch)",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(summary = "공연 홍보 지도상 검색 (Elasticsearch 기반)")
    @GetMapping("/map-v2")
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
        
        // Elasticsearch에서 지도 기반 검색
        Page<com.jandi.band_backend.search.document.PromoDocument> promoDocuments = 
            promoSearchService.filterPromosByLocation(startLatitude, startLongitude, endLatitude, endLongitude, pageable);
        
        // PromoDocument를 PromoRespDTO로 변환 (사용자별 좋아요 상태 포함)
        List<PromoRespDTO> promoRespDTOs = promoDocuments.getContent().stream()
                .map(doc -> {
                    PromoRespDTO dto = convertToPromoRespDTO(doc);
                    // 사용자별 좋아요 상태 확인
                    if (userId != null) {
                        Boolean isLikedByUser = promoLikeService.isLikedByUser(Integer.valueOf(doc.getId()), userId);
                        dto.setIsLikedByUser(isLikedByUser);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        
        Page<PromoRespDTO> promoPage = new org.springframework.data.domain.PageImpl<>(
            promoRespDTOs, pageable, promoDocuments.getTotalElements());
        
        return ResponseEntity.ok(CommonRespDTO.success("공연 홍보 지도 검색 성공 (Elasticsearch)",
                PagedRespDTO.from(promoPage)));
    }

    @Operation(summary = "공연 상태별 필터링 (Elasticsearch 기반)", 
               description = "공연 상태에 따라 필터링: ongoing(진행 중), upcoming(예정), ended(종료)")
    @GetMapping("/status-v2")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<PromoRespDTO>>> filterPromosByStatus(
            @RequestParam String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String teamName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "eventDate,asc") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Pageable pageable = createPageable(page, size, sort);
        Integer userId = userDetails != null ? userDetails.getUserId() : null;
        
        // Elasticsearch에서 공연 상태별 필터링
        Page<com.jandi.band_backend.search.document.PromoDocument> promoDocuments;
        
        if ((keyword != null && !keyword.trim().isEmpty()) || (teamName != null && !teamName.trim().isEmpty())) {
            // 추가 조건이 있는 경우
            promoDocuments = promoSearchService.filterPromosByStatusWithConditions(status, keyword, teamName, pageable);
        } else {
            // 상태만으로 필터링
            promoDocuments = promoSearchService.filterPromosByStatus(status, pageable);
        }
        
        // PromoDocument를 PromoRespDTO로 변환 (사용자별 좋아요 상태 포함)
        List<PromoRespDTO> promoRespDTOs = promoDocuments.getContent().stream()
                .map(doc -> {
                    PromoRespDTO dto = convertToPromoRespDTO(doc);
                    // 사용자별 좋아요 상태 확인
                    if (userId != null) {
                        Boolean isLikedByUser = promoLikeService.isLikedByUser(Integer.valueOf(doc.getId()), userId);
                        dto.setIsLikedByUser(isLikedByUser);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        
        Page<PromoRespDTO> promoPage = new org.springframework.data.domain.PageImpl<>(
            promoRespDTOs, pageable, promoDocuments.getTotalElements());
        
        String statusMessage = switch (status.toLowerCase()) {
            case "ongoing" -> "진행 중인 공연";
            case "upcoming" -> "예정된 공연";
            case "ended" -> "종료된 공연";
            default -> "공연";
        };
        
        return ResponseEntity.ok(CommonRespDTO.success(statusMessage + " 필터링 성공 (Elasticsearch)",
                PagedRespDTO.from(promoPage)));
    }

    private PromoRespDTO convertToPromoRespDTO(com.jandi.band_backend.search.document.PromoDocument doc) {
        PromoRespDTO dto = new PromoRespDTO();
        dto.setId(Integer.valueOf(doc.getId()));
        dto.setTeamName(doc.getTeamName());
        dto.setTitle(doc.getTitle());
        dto.setDescription(doc.getDescription());
        dto.setLocation(doc.getLocation());
        dto.setAddress(doc.getAddress());
        dto.setLatitude(doc.getLatitude());
        dto.setLongitude(doc.getLongitude());
        dto.setAdmissionFee(doc.getAdmissionFee());
        // LocalDate를 LocalDateTime으로 변환 (시간은 00:00:00으로 설정)
        dto.setEventDatetime(doc.getEventDate() != null ? doc.getEventDate().atStartOfDay() : null);
        dto.setCreatedAt(doc.getCreatedAt() != null ? doc.getCreatedAt().atStartOfDay() : null);
        dto.setUpdatedAt(doc.getUpdatedAt() != null ? doc.getUpdatedAt().atStartOfDay() : null);
        dto.setLikeCount(doc.getLikeCount());
        
        // 이미지 URL 설정
        if (doc.getImageUrl() != null) {
            dto.setPhotoUrls(List.of(doc.getImageUrl()));
        } else {
            dto.setPhotoUrls(List.of());
        }
        
        return dto;
    }

    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String field = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        return PageRequest.of(page, size, Sort.by(direction, field));
    }
    */
} 