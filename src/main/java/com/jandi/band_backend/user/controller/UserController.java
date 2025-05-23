package com.jandi.band_backend.user.controller;

import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import com.jandi.band_backend.user.dto.UpdateUserInfoReqDTO;
import com.jandi.band_backend.user.dto.UserInfoDTO;
import com.jandi.band_backend.user.service.UserPhotoService;
import com.jandi.band_backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User API", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserPhotoService userPhotoService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보 조회")
    @GetMapping("/me/info")
    public ApiResponse<UserInfoDTO> getMyInfo(
            @Parameter(description = "Bearer 토큰") @RequestHeader("Authorization") String token
    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        // 유저 기본 정보 및 프로필 정보 조회
        UserInfoDTO userInfo = new UserInfoDTO(
                userService.getMyInfo(kakaoOauthId),
                userPhotoService.getMyPhoto(kakaoOauthId)
        );
        return ApiResponse.success("내 정보 조회 성공", userInfo);
    }

    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보 수정")
    @PatchMapping("/me/info")
    public ApiResponse<UserInfoDTO> updateMyInfo(
            @Parameter(description = "Bearer 토큰") @RequestHeader("Authorization") String token,
            @Parameter(description = "수정할 사용자 정보") @ModelAttribute UpdateUserInfoReqDTO updateDTO,
            @Parameter(description = "프로필 사진 파일 (선택사항)") @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto

    ) {
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);

        // 유저 기본 정보 및 프로필 사진 수정
        userService.updateMyInfo(kakaoOauthId, updateDTO);
        userPhotoService.updateMyPhoto(kakaoOauthId, profilePhoto);
        return ApiResponse.success("내 정보 수정 성공");
    }
}
