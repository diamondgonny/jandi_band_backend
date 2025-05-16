package com.jandi.band_backend.auth.controller;

import com.jandi.band_backend.auth.dto.AuthRespDTO;
import com.jandi.band_backend.auth.dto.RefreshReqDTO;
import com.jandi.band_backend.auth.dto.SignUpReqDTO;
import com.jandi.band_backend.auth.dto.UserInfoDTO;
import com.jandi.band_backend.auth.service.AuthService;
import com.jandi.band_backend.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(token.replace("Bearer ", ""));
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
