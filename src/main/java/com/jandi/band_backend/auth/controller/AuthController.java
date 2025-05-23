package com.jandi.band_backend.auth.controller;

import com.jandi.band_backend.auth.dto.TokenRespDTO;
import com.jandi.band_backend.auth.dto.RefreshReqDTO;
import com.jandi.band_backend.auth.dto.SignUpReqDTO;
import com.jandi.band_backend.global.ApiResponse;
import com.jandi.band_backend.user.dto.UserInfoDTO;
import com.jandi.band_backend.auth.service.AuthService;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "카카오 로그인")
    @GetMapping("/login")
    public ApiResponse<TokenRespDTO> kakaoLogin(@RequestParam String code){
        TokenRespDTO tokens = authService.login(code);
        return ApiResponse.success("로그인 성공", tokens);
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ApiResponse<UserInfoDTO> signUp(
            @RequestHeader("Authorization") String token,
            @RequestBody SignUpReqDTO signUpReqDTO
    ){
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);
        UserInfoDTO userInfo = authService.signup(kakaoOauthId, signUpReqDTO);
        return ApiResponse.success("회원가입 성공", userInfo);
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ApiResponse<TokenRespDTO> refresh(@RequestBody RefreshReqDTO refreshReqDTO){
        String refreshToken = refreshReqDTO.getRefreshToken();
        TokenRespDTO tokens = authService.refresh(refreshToken);
        return ApiResponse.success("토큰 재발급 성공", tokens);
    }
}
