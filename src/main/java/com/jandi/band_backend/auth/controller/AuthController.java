package com.jandi.band_backend.auth.controller;

import com.jandi.band_backend.auth.dto.TokenRespDTO;
import com.jandi.band_backend.auth.dto.RefreshReqDTO;
import com.jandi.band_backend.auth.dto.SignUpReqDTO;
import com.jandi.band_backend.auth.dto.kakao.KakaoTokenRespDTO;
import com.jandi.band_backend.auth.dto.kakao.KakaoUserInfoDTO;
import com.jandi.band_backend.auth.service.kakao.KaKaoTokenService;
import com.jandi.band_backend.auth.service.kakao.KakaoUserService;
import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.user.dto.UserInfoDTO;
import com.jandi.band_backend.auth.service.AuthService;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final KaKaoTokenService kaKaoTokenService;
    private final KakaoUserService kakaoUserService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "카카오 로그인")
    @GetMapping("/login")
    public CommonResponse<TokenRespDTO> kakaoLogin(
            @RequestParam String code
    ){
        // 카카오로부터 유저 정보 얻기
        KakaoTokenRespDTO kakaoToken = kaKaoTokenService.getKakaoToken(code);
        KakaoUserInfoDTO kakaoUserInfo = kakaoUserService.getKakaoUserInfo(kakaoToken.getAccessToken());

        TokenRespDTO tokens = authService.login(kakaoUserInfo);
        return CommonResponse.success("로그인 성공", tokens);
    }

    @Operation(summary = "회원가입")
    @PostMapping("/logout")
    public CommonResponse<String> logout(
            @RequestHeader("Authorization") String token
    ){
        String accessToken = token.replace("Bearer ", "");
        authService.logout(accessToken);
        return CommonResponse.success("로그아웃 완료");

    }

    @PostMapping("/signup")
    public CommonResponse<UserInfoDTO> signUp(
            @RequestHeader("Authorization") String token,
            @RequestBody SignUpReqDTO signUpReqDTO
    ){
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);
        UserInfoDTO userInfo = authService.signup(kakaoOauthId, signUpReqDTO);
        return CommonResponse.success("회원가입 성공", userInfo);
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public CommonResponse<TokenRespDTO> refresh(
            @RequestBody RefreshReqDTO refreshReqDTO
    ){
        String refreshToken = refreshReqDTO.getRefreshToken();
        TokenRespDTO tokens = authService.refresh(refreshToken);
        return CommonResponse.success("토큰 재발급 성공", tokens);
    }
}
