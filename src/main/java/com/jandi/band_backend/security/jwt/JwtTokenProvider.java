package com.jandi.band_backend.security.jwt;

import com.jandi.band_backend.global.exception.InvalidTokenException;
import com.jandi.band_backend.security.CustomUserDetails;
import com.jandi.band_backend.security.CustomUserDetailsService;
import com.jandi.band_backend.user.entity.Users;
import com.jandi.band_backend.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key secretKey;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    // 액세스 토큰 유효기간 (15분)
    private final long validityInMilliseconds = 15 * 60 * 1000;

    // 리프레시 토큰 유효기간 (7일)
    private final long refreshValidityInMilliseconds = 7 * 24 * 60 * 60 * 1000;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            UserRepository userRepository,
            CustomUserDetailsService userDetailsService
    ) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    public String generateAccessToken(String kakaoOauthId) {
        log.info("카카오 계정 '{}' 에 대해 액세스 JWT 토큰 생성 시작", kakaoOauthId);

        Users user = userRepository.findByKakaoOauthId(kakaoOauthId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다"));

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);
        String role = "ROLE_" + user.getAdminRole().name();

        String token = Jwts.builder()
                .setSubject(kakaoOauthId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey)
                .compact();

        log.info("토큰 생성 - 사용자 카카오 계정: {}, 역할: {}", kakaoOauthId, role);
        log.info("액세스 토큰 생성 완료. 만료 시간: {}", expiry);
        return token;
    }

    public String generateRefreshToken(String kakaoOauthId) {
        log.info("카카오 계정 '{}' 에 대해 액세스 JWT 토큰 생성 시작", kakaoOauthId);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshValidityInMilliseconds);

        String token = Jwts.builder()
                .setSubject(kakaoOauthId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey)
                .compact();

        log.info("리프레시 토큰 생성 완료. 만료 시간: {}", expiry);
        return token;
    }

    public String getKakaoOauthId(String token) {
        try {
            if(!validateToken(token))
                throw new InvalidTokenException();

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.debug("토큰에서 추출한 카카오 계정: {}", claims.getSubject());
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("토큰에서 카카오 계정 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            log.debug("JWT 토큰 유효함");
            return true;
        } catch (Exception e) {
            log.error("JWT 토큰 유효성 검사 실패: {}", e.getMessage());
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        // 예외를 그대로 전파하여 필터에서 처리할 수 있도록 함
        String kakaoOauthId = getKakaoOauthId(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(kakaoOauthId);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }
}