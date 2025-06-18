package com.jandi.band_backend.search.controller;

import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.search.service.PromoSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "공연 홍보 검색 동기화", description = "Elasticsearch 동기화 관련 API")
@RestController
@RequestMapping("/api/admin/promos")
@RequiredArgsConstructor
public class PromoSyncController {

    private final PromoSyncService promoSyncService;

    /*
    @Operation(summary = "모든 데이터 동기화")
    @PostMapping("/sync-all")
    public ResponseEntity<CommonRespDTO<String>> syncAllPromos() {
        promoSyncService.syncAllPromos();
        return ResponseEntity.ok(CommonRespDTO.success("모든 데이터 동기화 완료", "데이터베이스의 모든 공연 홍보가 Elasticsearch에 동기화되었습니다."));
    }
    */
} 