package com.jandi.band_backend.univ.controller;

import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.univ.dto.RegionRespDTO;
import com.jandi.band_backend.univ.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Region API")
@RestController
@RequestMapping("/api/region")
@RequiredArgsConstructor
public class RegionController {
    private final RegionService regionService;

    @Operation(summary = "전체 지역 목록 조회")
    @GetMapping("/all")
    public CommonResponse<List<RegionRespDTO>> getAllRegion() {
        List<RegionRespDTO> regionList = regionService.getAllRegions();
        return CommonResponse.success("지역 리스트 조회 성공", regionList);
    }
}
