package com.jandi.band_backend.univ.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.univ.dto.RegionRespDTO;
import com.jandi.band_backend.univ.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Region API", description = "지역 정보 API")
@RestController
@RequestMapping("/api/region")
@RequiredArgsConstructor
public class RegionController {
    private final RegionService regionService;

    @Operation(
        summary = "전체 지역 목록 조회",
        description = "시스템에 등록된 모든 지역 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "지역 리스트 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/all")
    public ApiResponse<List<RegionRespDTO>> getAllRegion() {
        List<RegionRespDTO> regionList = regionService.getAllRegions();
        return ApiResponse.success("지역 리스트 조회 성공", regionList);
    }
}
