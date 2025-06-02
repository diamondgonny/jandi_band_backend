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

        UserInfoDTO userInfo = new UserInfoDTO(
                userService.getMyInfo(userId),
                userPhotoService.getMyPhoto(userId)
        );
        return CommonRespDTO.success("내 정보 조회 성공", userInfo);
    }

    @Operation(summary = "내 정보 수정")
    @PatchMapping("/me/info")
    public CommonRespDTO<?> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "university", required = false) String university,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto
    ) {
        Integer userId = userDetails.getUserId();

        // DTO 생성
        UpdateUserInfoReqDTO updateDTO = new UpdateUserInfoReqDTO();
        updateDTO.setNickname(nickname);
        updateDTO.setUniversity(university);
        updateDTO.setPosition(position);
        updateDTO.setProfilePhoto(profilePhoto);

        Integer mask = 0;
        mask += userService.updateMyInfo(userId, updateDTO);
        mask += userPhotoService.updateMyPhoto(userId, profilePhoto);

        int maskCopy = mask;
        String updateList = "";
        if ((maskCopy / 1000) % 10 > 0) updateList += "이미지 ";
        if ((maskCopy / 100) % 10 > 0) updateList += "포지션 ";
        if ((maskCopy / 10) % 10 > 0) updateList += "대학 ";
        if (maskCopy % 10 > 0) updateList += "닉네임 ";

        UserInfoDTO userInfo = new UserInfoDTO(
                userService.getMyInfo(userId),
                userPhotoService.getMyPhoto(userId)
        );
        return CommonRespDTO.success("내 정보 수정 성공: " + updateList, userInfo);
    }
}
