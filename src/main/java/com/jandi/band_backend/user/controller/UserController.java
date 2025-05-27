package com.jandi.band_backend.user.controller;

import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.security.CustomUserDetails;
import com.jandi.band_backend.user.dto.UpdateUserInfoReqDTO;
import com.jandi.band_backend.user.dto.UserInfoDTO;
import com.jandi.band_backend.user.service.UserPhotoService;
import com.jandi.band_backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserPhotoService userPhotoService;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me/info")
    public CommonRespDTO<UserInfoDTO> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = userDetails.getUserId();

        // 유저 기본 정보 및 프로필 정보 조회
        UserInfoDTO userInfo = new UserInfoDTO(
                userService.getMyInfo(userId),
                userPhotoService.getMyPhoto(userId)
        );
        return CommonRespDTO.success("내 정보 조회 성공", userInfo);
    }

    @Operation(summary = "내 정보 수정")
    @PatchMapping("/me/info")
    public CommonRespDTO<UserInfoDTO> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute UpdateUserInfoReqDTO updateDTO,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto

    ) {
        Integer userId = userDetails.getUserId();

        // 유저 기본 정보 및 프로필 사진 수정
        userService.updateMyInfo(userId, updateDTO);
        userPhotoService.updateMyPhoto(userId, profilePhoto);
        return CommonRespDTO.success("내 정보 수정 성공");
    }
}
