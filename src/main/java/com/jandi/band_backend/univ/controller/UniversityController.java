package com.jandi.band_backend.univ.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.univ.dto.UniversityDetailRespDTO;
import com.jandi.band_backend.univ.dto.UniversityRespDTO;
import com.jandi.band_backend.univ.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "University API")
@RestController
@RequestMapping("/api/univ")
@RequiredArgsConstructor
public class UniversityController {
    private final UniversityService universityService;

    @Operation(summary = "대학교 목록 조회")
    @GetMapping("/all")
    public ApiResponse<List<UniversityRespDTO>> getAllUniversity(
            @RequestParam(defaultValue = "ALL") String filter,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String region
    ) {
        List<UniversityRespDTO> univList = universityService.getAllUniversity(filter, type, region);
        return ApiResponse.success("대학 정보 조회 성공", univList);
    }

    @Operation(summary = "대학교 상세 정보 조회")
    @GetMapping("/{univId}")
    public ApiResponse<UniversityDetailRespDTO> getUniversityById(@PathVariable Integer univId) {
        UniversityDetailRespDTO universityById = universityService.getUniversityById(univId);
        return ApiResponse.success("대학 상세 정보 조회 성공", universityById);
    }
}
