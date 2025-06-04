package com.jandi.band_backend.club.controller;

import com.jandi.band_backend.club.dto.ClubGalPhotoReqDTO;
import com.jandi.band_backend.club.dto.ClubGalPhotoRespDTO;
import com.jandi.band_backend.club.dto.ClubGalPhotoRespDetailDTO;
import com.jandi.band_backend.club.service.ClubGalPhotoService;
import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.global.dto.PagedRespDTO;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Club Photo API")
@RestController
@RequestMapping("/api/clubs/{clubId}")
@RequiredArgsConstructor
public class ClubGalPhotoController {
    private final ClubGalPhotoService clubGalPhotoService;

    @Operation(summary = "동아리 사진 목록 조회")
    @GetMapping("/photo")
    public ResponseEntity<CommonRespDTO<PagedRespDTO<ClubGalPhotoRespDTO>>> getClubGalPhotoList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer clubId,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Integer userId = userDetails.getUserId();

        Page<ClubGalPhotoRespDTO> response = clubGalPhotoService.getClubGalPhotoList(clubId, userId, pageable);
        return ResponseEntity.ok(CommonRespDTO.success("동아리 사진 목록 조회 성공", PagedRespDTO.from(response)));
    }

    @Operation(summary = "동아리 사진 상세 조회")
    @GetMapping("/photo/{photoId}")
    public ResponseEntity<CommonRespDTO<ClubGalPhotoRespDetailDTO>> getClubGalPhotoDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer clubId,
            @PathVariable Integer photoId
    ) {
        Integer userId = userDetails.getUserId();

        ClubGalPhotoRespDetailDTO response = clubGalPhotoService.getClubGalPhotoDetail(clubId, userId, photoId);
        return ResponseEntity.ok(CommonRespDTO.success("동아리 사진 상세 조회 성공", response));
    }

    @Operation(summary = "동아리 사진 생성")
    @PostMapping("/photo")
    public ResponseEntity<CommonRespDTO<ClubGalPhotoRespDTO>> createClubGalPhotoList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer clubId,
            @RequestParam MultipartFile image,
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam(required = false, defaultValue = "true") Boolean isPublic
    ) {
        Integer userId = userDetails.getUserId();
        ClubGalPhotoReqDTO reqDTO = new ClubGalPhotoReqDTO(image, description, isPublic);

        ClubGalPhotoRespDTO response = clubGalPhotoService.createClubGalPhotoList(clubId, userId, reqDTO);
        return ResponseEntity.ok(CommonRespDTO.success("동아리 사진 생성 성공", response));
    }

    @Operation(summary = "동아리 사진 수정")
    @PatchMapping("/photo/{photoId}")
    public ResponseEntity<CommonRespDTO<ClubGalPhotoRespDetailDTO>> updateClubGalPhotoList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer clubId,
            @PathVariable Integer photoId,
            @RequestParam(required = false, defaultValue = "") MultipartFile image,
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam(required = false, defaultValue = "true") Boolean isPublic
    ) {
        Integer userId = userDetails.getUserId();
        ClubGalPhotoReqDTO reqDTO = new ClubGalPhotoReqDTO(image, description, isPublic);

        ClubGalPhotoRespDetailDTO response = clubGalPhotoService.updateClubGalPhoto(clubId, userId, photoId, reqDTO);
        return ResponseEntity.ok(CommonRespDTO.success("동아리 사진 수정 성공", response));
    }
}
