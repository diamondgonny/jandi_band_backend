package com.jandi.band_backend.univ.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.univ.dto.UniversityDetailRespDTO;
import com.jandi.band_backend.univ.dto.UniversityRespDTO;
import com.jandi.band_backend.univ.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "University API", description = "대학교 정보 API")
@RestController
@RequestMapping("/api/univ")
@RequiredArgsConstructor
public class UniversityController {
    private final UniversityService universityService;

    @Operation(
        summary = "대학교 목록 조회",
        description = "필터 조건에 따라 대학교 목록을 조회합니다. " +
                     "필터: ALL(전체), type(대학 유형), region(지역)으로 필터링할 수 있습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "대학 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 필터 조건",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/all")
    public ApiResponse<List<UniversityRespDTO>> getAllUniversity(
            @Parameter(description = "필터링 범위", required = false, example = "ALL")
            @RequestParam(defaultValue = "ALL") String filter, // 필터링 범위
            @Parameter(description = "대학 유형", required = false, example = "국립")
            @RequestParam(required = false) String type, // 대학 유형
            @Parameter(description = "대학 소재지", required = false, example = "서울")
            @RequestParam(required = false) String region // 대학 소재지
    ) {
        List<UniversityRespDTO> univList = universityService.getAllUniversity(filter, type, region);
        return ApiResponse.success("대학 정보 조회 성공", univList);
    }

    @Operation(
        summary = "대학교 상세 정보 조회",
        description = "특정 대학교의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "대학 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "대학을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/{univId}")
    public ApiResponse<UniversityDetailRespDTO> getUniversityById(
            @Parameter(description = "대학교 ID", required = true, example = "1")
            @PathVariable Integer univId
    ) {
        UniversityDetailRespDTO universityById = universityService.getUniversityById(univId);
        return ApiResponse.success("대학 상세 정보 조회 성공", universityById);
    }
}
