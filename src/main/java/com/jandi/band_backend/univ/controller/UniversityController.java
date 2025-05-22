package com.jandi.band_backend.univ.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.univ.dto.UniversityDetailRespDTO;
import com.jandi.band_backend.univ.dto.UniversityRespDTO;
import com.jandi.band_backend.univ.entity.University;
import com.jandi.band_backend.univ.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/univ")
@RequiredArgsConstructor
public class UniversityController {
    private final UniversityService universityService;

    @GetMapping("/all")
    public ApiResponse<List<UniversityRespDTO>> getAllUniversity() {
        List<UniversityRespDTO> univList = universityService.getAllUniversity();
        return ApiResponse.success("대학 정보 조회 성공", univList);
    }

    @GetMapping("/{univId}")
    public ApiResponse<UniversityDetailRespDTO> getUniversityById(
            @PathVariable Integer univId
    ) {
        UniversityDetailRespDTO universityById = universityService.getUniversityById(univId);
        return ApiResponse.success("대학 상세 정보 조회 성공", universityById);
    }
}
