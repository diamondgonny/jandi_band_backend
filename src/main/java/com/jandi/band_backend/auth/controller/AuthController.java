package com.jandi.band_backend.auth.controller;

import com.jandi.band_backend.auth.dto.AuthRespDTO;
import com.jandi.band_backend.auth.dto.RefreshReqDTO;
import com.jandi.band_backend.auth.dto.SignUpReqDTO;
import com.jandi.band_backend.user.dto.UserInfoDTO;
import com.jandi.band_backend.auth.service.AuthService;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/login")
    public AuthRespDTO kakaoLogin(
            @RequestParam String code
    ){
        return authService.login(code);
    }

    @PostMapping("/signup")
    public UserInfoDTO signUp(
            @RequestHeader("Authorization") String token,
            @RequestBody SignUpReqDTO signUpReqDTO
    ){
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);
        return authService.signup(kakaoOauthId, signUpReqDTO);
    }

    @PostMapping("/refresh")
    public AuthRespDTO refresh(
            @RequestBody RefreshReqDTO refreshReqDTO
    ){
        String refreshToken = refreshReqDTO.getRefreshToken();
        return authService.refresh(refreshToken);
    }
}
