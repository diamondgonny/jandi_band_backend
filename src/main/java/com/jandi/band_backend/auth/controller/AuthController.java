package com.jandi.band_backend.auth.controller;

import com.jandi.band_backend.auth.dto.TokenRespDTO;
import com.jandi.band_backend.auth.dto.RefreshReqDTO;
import com.jandi.band_backend.auth.dto.SignUpReqDTO;
import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.user.dto.UserInfoDTO;
import com.jandi.band_backend.auth.service.AuthService;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "카카오 로그인", description = "카카오 OAuth 인증 코드로 로그인")
    @GetMapping("/login")
    public ApiResponse<TokenRespDTO> kakaoLogin(
            @Parameter(description = "카카오 OAuth 인증 코드") @RequestParam String code
    ){
        TokenRespDTO tokens = authService.login(code);
        return ApiResponse.success("로그인 성공", tokens);
    }

    @Operation(summary = "회원가입", description = "카카오 로그인 후 추가 정보로 회원가입 완료")
    @PostMapping("/signup")
    public ApiResponse<UserInfoDTO> signUp(
            @Parameter(description = "카카오 로그인으로 받은 임시 토큰") @RequestHeader("Authorization") String token,
            @Parameter(description = "회원가입 요청 정보") @RequestBody SignUpReqDTO signUpReqDTO
    ){
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);
        UserInfoDTO userInfo = authService.signup(kakaoOauthId, signUpReqDTO);
        return ApiResponse.success("회원가입 성공", userInfo);
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access Token 재발급")
    @PostMapping("/refresh")
    public ApiResponse<TokenRespDTO> refresh(
            @Parameter(description = "토큰 재발급 요청 정보") @RequestBody RefreshReqDTO refreshReqDTO
    ){
        String refreshToken = refreshReqDTO.getRefreshToken();
        TokenRespDTO tokens = authService.refresh(refreshToken);
        return ApiResponse.success("토큰 재발급 성공", tokens);
    }
}
