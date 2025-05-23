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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
        summary = "카카오 로그인",
        description = "카카오 OAuth 인증 코드를 통해 로그인을 처리합니다. " +
                     "카카오에서 전달받은 인증 코드를 사용하여 사용자 정보를 조회하고 JWT 토큰을 발급합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 인증 코드",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/login")
    public ApiResponse<TokenRespDTO> kakaoLogin(
            @Parameter(description = "카카오 OAuth 인증 코드", required = true, example = "abc123def456")
            @RequestParam String code
    ){
        TokenRespDTO tokens = authService.login(code);
        return ApiResponse.success("로그인 성공", tokens);
    }

    @Operation(
        summary = "회원가입",
        description = "카카오 로그인 후 추가 정보를 입력하여 회원가입을 완료합니다. " +
                     "카카오 로그인으로 받은 임시 토큰과 함께 사용자의 포지션과 대학교 정보를 전달해야 합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 토큰",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "이미 가입된 사용자",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PostMapping("/signup")
    public ApiResponse<UserInfoDTO> signUp(
            @Parameter(description = "Bearer 토큰 (카카오 로그인으로 받은 임시 토큰)", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "회원가입 요청 정보", required = true)
            @RequestBody SignUpReqDTO signUpReqDTO
    ){
        String accessToken = token.replace("Bearer ", "");
        String kakaoOauthId = jwtTokenProvider.getKakaoOauthId(accessToken);
        UserInfoDTO userInfo = authService.signup(kakaoOauthId, signUpReqDTO);
        return ApiResponse.success("회원가입 성공", userInfo);
    }

    @Operation(
        summary = "토큰 재발급",
        description = "만료된 Access Token을 Refresh Token을 사용하여 재발급합니다. " +
                     "Refresh Token이 유효한 경우 새로운 Access Token과 Refresh Token을 반환합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "토큰 재발급 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 Refresh Token",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @PostMapping("/refresh")
    public ApiResponse<TokenRespDTO> refresh(
            @Parameter(description = "토큰 재발급 요청 정보", required = true)
            @RequestBody RefreshReqDTO refreshReqDTO
    ){
        String refreshToken = refreshReqDTO.getRefreshToken();
        TokenRespDTO tokens = authService.refresh(refreshToken);
        return ApiResponse.success("토큰 재발급 성공", tokens);
    }
}
